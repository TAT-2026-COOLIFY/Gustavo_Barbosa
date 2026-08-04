package com.ironfit.api.dieta.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.dto.DiarioDiaResponseDTO;
import com.ironfit.api.dieta.dto.DiarioHistoricoDiaDTO;
import com.ironfit.api.dieta.dto.DiarioRefeicaoDTO;
import com.ironfit.api.dieta.dto.RefeicaoResponseDTO;
import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.entity.Refeicao;
import com.ironfit.api.dieta.entity.RegistroRefeicao;
import com.ironfit.api.dieta.mapper.RefeicaoMapper;
import com.ironfit.api.dieta.repository.DietaRepository;
import com.ironfit.api.dieta.repository.RefeicaoAlimentoRepository;
import com.ironfit.api.dieta.repository.RefeicaoRepository;
import com.ironfit.api.dieta.repository.RegistroRefeicaoRepository;
import com.ironfit.api.dieta.mapper.RefeicaoAlimentoMapper;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Diário alimentar: acompanha, dia a dia, quais refeições da dieta ativa o usuário efetivamente consumiu. */
@Service
public class DiarioService {

    private final DietaRepository dietaRepository;
    private final RefeicaoRepository refeicaoRepository;
    private final RefeicaoAlimentoRepository refeicaoAlimentoRepository;
    private final RegistroRefeicaoRepository registroRefeicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RefeicaoMapper refeicaoMapper;
    private final RefeicaoAlimentoMapper refeicaoAlimentoMapper;

    public DiarioService(
            DietaRepository dietaRepository,
            RefeicaoRepository refeicaoRepository,
            RefeicaoAlimentoRepository refeicaoAlimentoRepository,
            RegistroRefeicaoRepository registroRefeicaoRepository,
            UsuarioRepository usuarioRepository,
            RefeicaoMapper refeicaoMapper,
            RefeicaoAlimentoMapper refeicaoAlimentoMapper) {
        this.dietaRepository = dietaRepository;
        this.refeicaoRepository = refeicaoRepository;
        this.refeicaoAlimentoRepository = refeicaoAlimentoRepository;
        this.registroRefeicaoRepository = registroRefeicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.refeicaoMapper = refeicaoMapper;
        this.refeicaoAlimentoMapper = refeicaoAlimentoMapper;
    }

    @Transactional(readOnly = true)
    public DiarioDiaResponseDTO obterDia(Long usuarioId, LocalDate data) {
        Dieta dietaAtiva = dietaRepository
                .findByUsuarioIdAndAtivaTrue(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma dieta ativa no momento"));

        Map<Long, RegistroRefeicao> registrosPorRefeicao =
                registroRefeicaoRepository.findByUsuarioIdAndDataConsumo(usuarioId, data).stream()
                        .collect(java.util.stream.Collectors.toMap(r -> r.getRefeicao().getId(), r -> r));

        List<DiarioRefeicaoDTO> refeicoes = refeicaoRepository.findByDietaIdOrderByOrdem(dietaAtiva.getId()).stream()
                .map(refeicao -> paraDiario(refeicao, registrosPorRefeicao.get(refeicao.getId())))
                .toList();

        BigDecimal caloriasConsumidas = somarConsumido(refeicoes, DiarioRefeicaoDTO::calorias);
        BigDecimal proteinaConsumida = somarConsumido(refeicoes, DiarioRefeicaoDTO::proteinaG);
        BigDecimal carboidratoConsumido = somarConsumido(refeicoes, DiarioRefeicaoDTO::carboidratoG);
        BigDecimal gorduraConsumida = somarConsumido(refeicoes, DiarioRefeicaoDTO::gorduraG);

        return new DiarioDiaResponseDTO(
                data,
                dietaAtiva.getId(),
                dietaAtiva.getNome(),
                dietaAtiva.getMetaCaloriasKcal(),
                dietaAtiva.getMetaProteinaG(),
                dietaAtiva.getMetaCarboidratoG(),
                dietaAtiva.getMetaGorduraG(),
                caloriasConsumidas,
                proteinaConsumida,
                carboidratoConsumido,
                gorduraConsumida,
                refeicoes);
    }

    @Transactional
    public DiarioRefeicaoDTO registrarConsumo(Long usuarioId, Long refeicaoId, LocalDate data) {
        Refeicao refeicao = buscarRefeicaoDaDietaAtiva(usuarioId, refeicaoId);

        Optional<RegistroRefeicao> existente =
                registroRefeicaoRepository.findByUsuarioIdAndRefeicaoIdAndDataConsumo(usuarioId, refeicaoId, data);
        RegistroRefeicao registro = existente.orElseGet(() -> {
            Usuario usuario = usuarioRepository
                    .findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            RegistroRefeicao novo =
                    RegistroRefeicao.builder().usuario(usuario).refeicao(refeicao).dataConsumo(data).build();
            return registroRefeicaoRepository.save(novo);
        });

        return paraDiario(refeicao, registro);
    }

    @Transactional
    public void removerConsumo(Long usuarioId, Long refeicaoId, LocalDate data) {
        registroRefeicaoRepository.deleteByUsuarioIdAndRefeicaoIdAndDataConsumo(usuarioId, refeicaoId, data);
    }

    @Transactional(readOnly = true)
    public List<DiarioHistoricoDiaDTO> historico(Long usuarioId, int dias) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(Math.max(dias, 1) - 1L);

        Integer metaCalorias = dietaRepository
                .findByUsuarioIdAndAtivaTrue(usuarioId)
                .map(Dieta::getMetaCaloriasKcal)
                .orElse(null);

        Map<LocalDate, BigDecimal> caloriasPorDia = new HashMap<>();
        for (RegistroRefeicao registro : registroRefeicaoRepository.findByUsuarioIdAndDataConsumoBetween(usuarioId, inicio, fim)) {
            BigDecimal caloriasRefeicao = calcularTotais(registro.getRefeicao()).caloriasTotal();
            caloriasPorDia.merge(registro.getDataConsumo(), caloriasRefeicao, BigDecimal::add);
        }

        return inicio.datesUntil(fim.plusDays(1))
                .map(data -> new DiarioHistoricoDiaDTO(
                        data, metaCalorias, caloriasPorDia.getOrDefault(data, BigDecimal.ZERO)))
                .toList();
    }

    private Refeicao buscarRefeicaoDaDietaAtiva(Long usuarioId, Long refeicaoId) {
        Refeicao refeicao = refeicaoRepository
                .findById(refeicaoId)
                .orElseThrow(() -> new ResourceNotFoundException("Refeição não encontrada"));

        Dieta dietaAtiva = dietaRepository
                .findByUsuarioIdAndAtivaTrue(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma dieta ativa no momento"));

        if (!refeicao.getDieta().getId().equals(dietaAtiva.getId())) {
            throw new UnauthorizedException("Refeição não pertence à dieta ativa do usuário");
        }
        return refeicao;
    }

    private DiarioRefeicaoDTO paraDiario(Refeicao refeicao, RegistroRefeicao registro) {
        RefeicaoResponseDTO totais = calcularTotais(refeicao);
        return new DiarioRefeicaoDTO(
                refeicao.getId(),
                refeicao.getNome(),
                refeicao.getHorarioSugerido(),
                totais.caloriasTotal(),
                totais.proteinaTotalG(),
                totais.carboidratoTotalG(),
                totais.gorduraTotalG(),
                registro != null,
                registro != null ? registro.getConsumidoEm() : null);
    }

    private RefeicaoResponseDTO calcularTotais(Refeicao refeicao) {
        var itens = refeicaoAlimentoRepository.findByRefeicaoIdOrderByOrdem(refeicao.getId()).stream()
                .map(refeicaoAlimentoMapper::toResponseDTO)
                .toList();
        return refeicaoMapper.toResponseDTO(refeicao, itens);
    }

    private BigDecimal somarConsumido(
            List<DiarioRefeicaoDTO> refeicoes, java.util.function.Function<DiarioRefeicaoDTO, BigDecimal> extrator) {
        return refeicoes.stream()
                .filter(DiarioRefeicaoDTO::consumida)
                .map(extrator)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
