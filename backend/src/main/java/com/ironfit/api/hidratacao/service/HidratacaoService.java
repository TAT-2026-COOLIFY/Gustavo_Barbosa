package com.ironfit.api.hidratacao.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.hidratacao.dto.AtualizarMetaHidratacaoRequestDTO;
import com.ironfit.api.hidratacao.dto.HistoricoDiaHidratacaoDTO;
import com.ironfit.api.hidratacao.dto.MetaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.RegistroHidratacaoRequestDTO;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.SequenciaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.entity.MetaHidratacao;
import com.ironfit.api.hidratacao.entity.RegistroHidratacao;
import com.ironfit.api.hidratacao.mapper.HidratacaoMapper;
import com.ironfit.api.hidratacao.repository.MetaHidratacaoRepository;
import com.ironfit.api.hidratacao.repository.RegistroHidratacaoRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Hidratação: meta diária de água, registro de consumo (copos) e acompanhamento (resumo do dia, histórico, sequência). */
@Service
public class HidratacaoService {

    private static final int META_PADRAO_ML = 2000;

    private final MetaHidratacaoRepository metaHidratacaoRepository;
    private final RegistroHidratacaoRepository registroHidratacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HidratacaoMapper hidratacaoMapper;

    public HidratacaoService(
            MetaHidratacaoRepository metaHidratacaoRepository,
            RegistroHidratacaoRepository registroHidratacaoRepository,
            UsuarioRepository usuarioRepository,
            HidratacaoMapper hidratacaoMapper) {
        this.metaHidratacaoRepository = metaHidratacaoRepository;
        this.registroHidratacaoRepository = registroHidratacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.hidratacaoMapper = hidratacaoMapper;
    }

    @Transactional
    public MetaHidratacaoResponseDTO obterMetaAtiva(Long usuarioId) {
        return hidratacaoMapper.toResponseDTO(obterOuCriarMetaAtivaEntidade(usuarioId));
    }

    @Transactional
    public MetaHidratacaoResponseDTO atualizarMeta(Long usuarioId, AtualizarMetaHidratacaoRequestDTO request) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        metaHidratacaoRepository.desativarTodasDoUsuario(usuarioId);

        MetaHidratacao meta = MetaHidratacao.builder()
                .usuario(usuario)
                .metaMl(request.metaMl())
                .ativo(true)
                .build();

        metaHidratacaoRepository.save(meta);
        return hidratacaoMapper.toResponseDTO(meta);
    }

    @Transactional
    public ResumoDiaHidratacaoResponseDTO registrar(Long usuarioId, RegistroHidratacaoRequestDTO request) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        RegistroHidratacao registro = RegistroHidratacao.builder()
                .usuario(usuario)
                .quantidadeMl(request.quantidadeMl())
                .dataRegistro(LocalDate.now())
                .build();

        registroHidratacaoRepository.save(registro);
        return obterResumoDia(usuarioId, LocalDate.now());
    }

    @Transactional
    public void removerRegistro(Long usuarioId, Long registroId) {
        RegistroHidratacao registro = registroHidratacaoRepository
                .findByIdAndUsuarioId(registroId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de hidratação não encontrado"));

        registroHidratacaoRepository.delete(registro);
    }

    @Transactional
    public ResumoDiaHidratacaoResponseDTO obterResumoDia(Long usuarioId, LocalDate data) {
        MetaHidratacao meta = obterOuCriarMetaAtivaEntidade(usuarioId);

        List<RegistroHidratacao> registros =
                registroHidratacaoRepository.findByUsuarioIdAndDataRegistroOrderByRegistradoEmDesc(usuarioId, data);

        int totalMl = registros.stream().mapToInt(RegistroHidratacao::getQuantidadeMl).sum();
        int percentual = calcularPercentual(totalMl, meta.getMetaMl());

        return new ResumoDiaHidratacaoResponseDTO(
                data,
                meta.getMetaMl(),
                totalMl,
                percentual,
                registros.stream().map(hidratacaoMapper::toResponseDTO).toList());
    }

    @Transactional
    public List<HistoricoDiaHidratacaoDTO> historico(Long usuarioId, int dias) {
        MetaHidratacao meta = obterOuCriarMetaAtivaEntidade(usuarioId);

        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusDays(Math.max(dias, 1) - 1L);

        Map<LocalDate, Integer> totalPorDia = new HashMap<>();
        for (RegistroHidratacao registro :
                registroHidratacaoRepository.findByUsuarioIdAndDataRegistroBetween(usuarioId, inicio, fim)) {
            totalPorDia.merge(registro.getDataRegistro(), registro.getQuantidadeMl(), Integer::sum);
        }

        return inicio.datesUntil(fim.plusDays(1))
                .map(data -> {
                    int totalMl = totalPorDia.getOrDefault(data, 0);
                    return new HistoricoDiaHidratacaoDTO(data, meta.getMetaMl(), totalMl, totalMl >= meta.getMetaMl());
                })
                .toList();
    }

    @Transactional
    public SequenciaHidratacaoResponseDTO calcularSequencia(Long usuarioId) {
        MetaHidratacao meta = obterOuCriarMetaAtivaEntidade(usuarioId);
        int metaMl = meta.getMetaMl();

        Map<LocalDate, Integer> totalPorDia = new HashMap<>();
        LocalDate hoje = LocalDate.now();
        // Janela de 365 dias é suficiente para qualquer sequência realista e evita varrer a tabela inteira.
        LocalDate inicioJanela = hoje.minusDays(364);
        for (RegistroHidratacao registro :
                registroHidratacaoRepository.findByUsuarioIdAndDataRegistroBetween(usuarioId, inicioJanela, hoje)) {
            totalPorDia.merge(registro.getDataRegistro(), registro.getQuantidadeMl(), Integer::sum);
        }

        int diasConsecutivos = 0;
        LocalDate dia = hoje;
        while (totalPorDia.getOrDefault(dia, 0) >= metaMl) {
            diasConsecutivos++;
            dia = dia.minusDays(1);
        }

        return new SequenciaHidratacaoResponseDTO(diasConsecutivos);
    }

    private MetaHidratacao obterOuCriarMetaAtivaEntidade(Long usuarioId) {
        return metaHidratacaoRepository.findByUsuarioIdAndAtivoTrue(usuarioId).orElseGet(() -> {
            Usuario usuario = usuarioRepository
                    .findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            MetaHidratacao padrao = MetaHidratacao.builder()
                    .usuario(usuario)
                    .metaMl(META_PADRAO_ML)
                    .ativo(true)
                    .build();
            return metaHidratacaoRepository.save(padrao);
        });
    }

    private int calcularPercentual(int totalMl, int metaMl) {
        if (metaMl <= 0) {
            return 0;
        }
        return Math.min(100, Math.round((totalMl * 100f) / metaMl));
    }
}
