package com.ironfit.api.dieta.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.common.FavoritoTipo;
import com.ironfit.api.common.service.FavoritoService;
import com.ironfit.api.dieta.dto.DietaRequestDTO;
import com.ironfit.api.dieta.dto.DietaResponseDTO;
import com.ironfit.api.dieta.dto.DietaResumoDTO;
import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.dieta.dto.RefeicaoAlimentoRequestDTO;
import com.ironfit.api.dieta.dto.RefeicaoAlimentoResponseDTO;
import com.ironfit.api.dieta.dto.RefeicaoRequestDTO;
import com.ironfit.api.dieta.dto.RefeicaoResponseDTO;
import com.ironfit.api.dieta.entity.Alimento;
import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.entity.OrigemDieta;
import com.ironfit.api.dieta.entity.Refeicao;
import com.ironfit.api.dieta.entity.RefeicaoAlimento;
import com.ironfit.api.dieta.mapper.DietaMapper;
import com.ironfit.api.dieta.mapper.RefeicaoAlimentoMapper;
import com.ironfit.api.dieta.mapper.RefeicaoMapper;
import com.ironfit.api.dieta.repository.AlimentoRepository;
import com.ironfit.api.dieta.repository.DietaRepository;
import com.ironfit.api.dieta.repository.RefeicaoAlimentoRepository;
import com.ironfit.api.dieta.repository.RefeicaoRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DietaService {

    private final DietaRepository dietaRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final RefeicaoAlimentoRepository refeicaoAlimentoRepository;
    private final AlimentoRepository alimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DietaMapper dietaMapper;
    private final RefeicaoMapper refeicaoMapper;
    private final RefeicaoAlimentoMapper refeicaoAlimentoMapper;
    private final FavoritoService favoritoService;
    private final CalculoNutricionalService calculoNutricionalService;

    public DietaService(
            DietaRepository dietaRepository,
            RefeicaoRepository refeicaoRepository,
            RefeicaoAlimentoRepository refeicaoAlimentoRepository,
            AlimentoRepository alimentoRepository,
            UsuarioRepository usuarioRepository,
            DietaMapper dietaMapper,
            RefeicaoMapper refeicaoMapper,
            RefeicaoAlimentoMapper refeicaoAlimentoMapper,
            FavoritoService favoritoService,
            CalculoNutricionalService calculoNutricionalService) {
        this.dietaRepository = dietaRepository;
        this.refeicaoRepository = refeicaoRepository;
        this.refeicaoAlimentoRepository = refeicaoAlimentoRepository;
        this.alimentoRepository = alimentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.dietaMapper = dietaMapper;
        this.refeicaoMapper = refeicaoMapper;
        this.refeicaoAlimentoMapper = refeicaoAlimentoMapper;
        this.favoritoService = favoritoService;
        this.calculoNutricionalService = calculoNutricionalService;
    }

    public List<DietaResumoDTO> listarMinhas(Long usuarioId) {
        return dietaRepository.findByUsuarioIdOrderByAtualizadoEmDesc(usuarioId).stream()
                .map(dieta -> toResumo(dieta, usuarioId))
                .toList();
    }

    public List<DietaResumoDTO> listarTemplates(Long usuarioId) {
        return dietaRepository.findByUsuarioIsNullAndOrigemOrderByNome(OrigemDieta.TEMPLATE).stream()
                .map(dieta -> toResumo(dieta, usuarioId))
                .toList();
    }

    @Transactional(readOnly = true)
    public DietaResponseDTO obterAtiva(Long usuarioId) {
        Dieta dieta = dietaRepository
                .findByUsuarioIdAndAtivaTrue(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma dieta ativa no momento"));
        return montarResponseDTO(dieta, usuarioId);
    }

    @Transactional(readOnly = true)
    public DietaResponseDTO obterDetalhe(Long usuarioId, Long dietaId) {
        return montarResponseDTO(buscarAcessivel(usuarioId, dietaId), usuarioId);
    }

    @Transactional
    public DietaResponseDTO criar(Long usuarioId, DietaRequestDTO request) {
        return criar(usuarioId, request, OrigemDieta.MANUAL);
    }

    /** Usado pelo módulo de IA para criar uma dieta já marcada com {@link OrigemDieta#IA} ao confirmar um rascunho. */
    @Transactional
    public DietaResponseDTO criar(Long usuarioId, DietaRequestDTO request, OrigemDieta origem) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Dieta dieta = Dieta.builder().usuario(usuario).nome(request.nome()).origem(origem).build();
        aplicarMetas(dieta, usuarioId, request);
        dietaRepository.save(dieta);

        var refeicoes = salvarRefeicoes(dieta, request.refeicoes());
        return dietaMapper.toResponseDTO(dieta, false, refeicoes);
    }

    @Transactional
    public DietaResponseDTO atualizar(Long usuarioId, Long dietaId, DietaRequestDTO request) {
        Dieta dieta = dietaRepository
                .findByIdAndUsuarioId(dietaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Dieta não encontrada"));

        dieta.setNome(request.nome());
        aplicarMetas(dieta, usuarioId, request);
        dietaRepository.save(dieta);

        refeicaoRepository.deleteByDietaId(dietaId);
        refeicaoRepository.flush();
        var refeicoes = salvarRefeicoes(dieta, request.refeicoes());

        boolean favorito = favoritoService.isFavorito(usuarioId, FavoritoTipo.DIETA, dietaId);
        return dietaMapper.toResponseDTO(dieta, favorito, refeicoes);
    }

    @Transactional
    public void excluir(Long usuarioId, Long dietaId) {
        Dieta dieta = dietaRepository
                .findByIdAndUsuarioId(dietaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Dieta não encontrada"));
        dietaRepository.delete(dieta);
    }

    @Transactional
    public DietaResponseDTO duplicar(Long usuarioId, Long dietaId) {
        Dieta origem = buscarAcessivel(usuarioId, dietaId);
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Dieta copia = Dieta.builder()
                .usuario(usuario)
                .nome(origem.getNome() + " (cópia)")
                .origem(OrigemDieta.MANUAL)
                .templateOrigemId(origem.getId())
                .pesoReferenciaKg(origem.getPesoReferenciaKg())
                .nivelAtividade(origem.getNivelAtividade())
                .tipoObjetivoReferencia(origem.getTipoObjetivoReferencia())
                .calculoAutomatico(origem.isCalculoAutomatico())
                .metaCaloriasKcal(origem.getMetaCaloriasKcal())
                .metaProteinaG(origem.getMetaProteinaG())
                .metaCarboidratoG(origem.getMetaCarboidratoG())
                .metaGorduraG(origem.getMetaGorduraG())
                .metaAguaMl(origem.getMetaAguaMl())
                .build();
        dietaRepository.save(copia);

        var refeicoesOrigem = refeicaoRepository.findByDietaIdOrderByOrdem(dietaId);
        var requests = refeicoesOrigem.stream()
                .map(refeicao -> {
                    var itensOrigem = refeicaoAlimentoRepository.findByRefeicaoIdOrderByOrdem(refeicao.getId());
                    var itens = itensOrigem.stream()
                            .map(item -> new RefeicaoAlimentoRequestDTO(
                                    item.getAlimento().getId(), item.getOrdem(), item.getQuantidadeGramas()))
                            .toList();
                    return new RefeicaoRequestDTO(refeicao.getNome(), refeicao.getOrdem(), refeicao.getHorarioSugerido(), itens);
                })
                .toList();

        var refeicoes = salvarRefeicoes(copia, requests);
        return dietaMapper.toResponseDTO(copia, false, refeicoes);
    }

    @Transactional
    public DietaResponseDTO ativar(Long usuarioId, Long dietaId) {
        Dieta dieta = dietaRepository
                .findByIdAndUsuarioId(dietaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Dieta não encontrada"));

        dietaRepository.desativarTodasDoUsuario(usuarioId);
        dieta.setAtiva(true);
        dietaRepository.save(dieta);

        return montarResponseDTO(dieta, usuarioId);
    }

    /** Retorna a dieta se pertencer ao usuário ou for uma dieta-modelo do sistema. */
    Dieta buscarAcessivel(Long usuarioId, Long dietaId) {
        Dieta dieta =
                dietaRepository.findById(dietaId).orElseThrow(() -> new ResourceNotFoundException("Dieta não encontrada"));

        boolean ehDoUsuario = dieta.getUsuario() != null && dieta.getUsuario().getId().equals(usuarioId);
        boolean ehTemplate = dieta.getUsuario() == null && dieta.getOrigem() == OrigemDieta.TEMPLATE;

        if (!ehDoUsuario && !ehTemplate) {
            throw new UnauthorizedException("Dieta não acessível para este usuário");
        }
        return dieta;
    }

    /** Preenche as metas nutricionais da dieta: calculadas automaticamente ou informadas manualmente. Pacote-visível para testes. */
    void aplicarMetas(Dieta dieta, Long usuarioId, DietaRequestDTO request) {
        dieta.setCalculoAutomatico(request.calculoAutomatico());
        dieta.setPesoReferenciaKg(request.pesoReferenciaKg());
        dieta.setNivelAtividade(request.nivelAtividade());
        dieta.setTipoObjetivoReferencia(request.tipoObjetivoReferencia());

        if (request.calculoAutomatico()) {
            if (request.pesoReferenciaKg() == null
                    || request.nivelAtividade() == null
                    || request.tipoObjetivoReferencia() == null) {
                throw new BusinessException(
                        "Peso de referência, nível de atividade e objetivo são obrigatórios para o cálculo automático");
            }
            MetaNutricionalCalculadaDTO meta = calculoNutricionalService.calcular(
                    usuarioId, request.pesoReferenciaKg(), request.nivelAtividade(), request.tipoObjetivoReferencia());
            dieta.setMetaCaloriasKcal(meta.metaCaloriasKcal());
            dieta.setMetaProteinaG(meta.metaProteinaG());
            dieta.setMetaCarboidratoG(meta.metaCarboidratoG());
            dieta.setMetaGorduraG(meta.metaGorduraG());
            dieta.setMetaAguaMl(meta.metaAguaMl());
        } else {
            if (request.metaCaloriasKcal() == null
                    || request.metaProteinaG() == null
                    || request.metaCarboidratoG() == null
                    || request.metaGorduraG() == null
                    || request.metaAguaMl() == null) {
                throw new BusinessException("Todas as metas nutricionais são obrigatórias quando o cálculo não é automático");
            }
            dieta.setMetaCaloriasKcal(request.metaCaloriasKcal());
            dieta.setMetaProteinaG(request.metaProteinaG());
            dieta.setMetaCarboidratoG(request.metaCarboidratoG());
            dieta.setMetaGorduraG(request.metaGorduraG());
            dieta.setMetaAguaMl(request.metaAguaMl());
        }
    }

    private List<RefeicaoResponseDTO> salvarRefeicoes(Dieta dieta, List<RefeicaoRequestDTO> requests) {
        return requests.stream().map(req -> salvarRefeicao(dieta, req)).toList();
    }

    private RefeicaoResponseDTO salvarRefeicao(Dieta dieta, RefeicaoRequestDTO request) {
        Refeicao refeicao = Refeicao.builder()
                .dieta(dieta)
                .nome(request.nome())
                .ordem(request.ordem())
                .horarioSugerido(request.horarioSugerido())
                .build();
        refeicaoRepository.save(refeicao);

        List<RefeicaoAlimentoResponseDTO> itens = request.alimentos().stream()
                .map(item -> salvarItem(refeicao, item))
                .toList();

        return refeicaoMapper.toResponseDTO(refeicao, itens);
    }

    private RefeicaoAlimentoResponseDTO salvarItem(Refeicao refeicao, RefeicaoAlimentoRequestDTO request) {
        Alimento alimento = alimentoRepository
                .findById(request.alimentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Alimento não encontrado: " + request.alimentoId()));

        RefeicaoAlimento refeicaoAlimento = RefeicaoAlimento.builder()
                .refeicao(refeicao)
                .alimento(alimento)
                .quantidadeGramas(request.quantidadeGramas())
                .ordem(request.ordem())
                .build();

        refeicaoAlimentoRepository.save(refeicaoAlimento);
        return refeicaoAlimentoMapper.toResponseDTO(refeicaoAlimento);
    }

    private DietaResponseDTO montarResponseDTO(Dieta dieta, Long usuarioId) {
        var refeicoes = refeicaoRepository.findByDietaIdOrderByOrdem(dieta.getId()).stream()
                .map(refeicao -> refeicaoMapper.toResponseDTO(refeicao, mapearItens(refeicao.getId())))
                .toList();
        boolean favorito = favoritoService.isFavorito(usuarioId, FavoritoTipo.DIETA, dieta.getId());
        return dietaMapper.toResponseDTO(dieta, favorito, refeicoes);
    }

    private List<RefeicaoAlimentoResponseDTO> mapearItens(Long refeicaoId) {
        return refeicaoAlimentoRepository.findByRefeicaoIdOrderByOrdem(refeicaoId).stream()
                .map(refeicaoAlimentoMapper::toResponseDTO)
                .toList();
    }

    private DietaResumoDTO toResumo(Dieta dieta, Long usuarioId) {
        int quantidade = refeicaoRepository.findByDietaIdOrderByOrdem(dieta.getId()).size();
        boolean favorito = favoritoService.isFavorito(usuarioId, FavoritoTipo.DIETA, dieta.getId());
        return dietaMapper.toResumoDTO(dieta, favorito, quantidade);
    }
}
