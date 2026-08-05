package com.ironfit.api.meditacao.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.meditacao.dto.ConcluirMeditacaoRequestDTO;
import com.ironfit.api.meditacao.dto.HistoricoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.dto.SequenciaMeditacaoResponseDTO;
import com.ironfit.api.meditacao.dto.SessaoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.entity.Meditacao;
import com.ironfit.api.meditacao.entity.SessaoMeditacao;
import com.ironfit.api.meditacao.mapper.MeditacaoMapper;
import com.ironfit.api.meditacao.repository.SessaoMeditacaoRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Execução de sessões de meditação: iniciar, concluir com a duração real, histórico e sequência de dias consecutivos. */
@Service
public class ExecucaoMeditacaoService {

    private final SessaoMeditacaoRepository sessaoMeditacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MeditacaoService meditacaoService;
    private final MeditacaoMapper meditacaoMapper;

    public ExecucaoMeditacaoService(
            SessaoMeditacaoRepository sessaoMeditacaoRepository,
            UsuarioRepository usuarioRepository,
            MeditacaoService meditacaoService,
            MeditacaoMapper meditacaoMapper) {
        this.sessaoMeditacaoRepository = sessaoMeditacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.meditacaoService = meditacaoService;
        this.meditacaoMapper = meditacaoMapper;
    }

    @Transactional
    public SessaoMeditacaoResponseDTO iniciar(Long usuarioId, Long meditacaoId) {
        Meditacao meditacao = meditacaoService.buscar(meditacaoId);
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        SessaoMeditacao sessao = SessaoMeditacao.builder()
                .usuario(usuario)
                .meditacao(meditacao)
                .dataRealizacao(Instant.now())
                .concluido(false)
                .build();
        sessaoMeditacaoRepository.save(sessao);

        return meditacaoMapper.toResponseDTO(sessao);
    }

    @Transactional(readOnly = true)
    public SessaoMeditacaoResponseDTO obter(Long usuarioId, Long sessaoId) {
        return meditacaoMapper.toResponseDTO(buscarSessaoDoUsuario(usuarioId, sessaoId));
    }

    @Transactional
    public SessaoMeditacaoResponseDTO concluir(Long usuarioId, Long sessaoId, ConcluirMeditacaoRequestDTO request) {
        SessaoMeditacao sessao = buscarSessaoDoUsuario(usuarioId, sessaoId);
        sessao.setConcluido(true);
        sessao.setDuracaoSegundos(request.duracaoSegundos());
        sessaoMeditacaoRepository.save(sessao);
        return meditacaoMapper.toResponseDTO(sessao);
    }

    @Transactional(readOnly = true)
    public List<HistoricoMeditacaoResponseDTO> historico(Long usuarioId) {
        return sessaoMeditacaoRepository.findByUsuarioIdAndConcluidoTrueOrderByDataRealizacaoDesc(usuarioId).stream()
                .map(sessao -> new HistoricoMeditacaoResponseDTO(
                        sessao.getId(),
                        sessao.getMeditacao().getNome(),
                        sessao.getMeditacao().getTipo(),
                        sessao.getDataRealizacao(),
                        sessao.getDuracaoSegundos(),
                        sessao.isConcluido()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SequenciaMeditacaoResponseDTO sequencia(Long usuarioId) {
        Instant desde = Instant.now().minus(365, ChronoUnit.DAYS);
        Set<LocalDate> diasComMeditacao =
                sessaoMeditacaoRepository
                        .findByUsuarioIdAndConcluidoTrueAndDataRealizacaoAfterOrderByDataRealizacaoDesc(usuarioId, desde)
                        .stream()
                        .map(sessao -> sessao.getDataRealizacao().atZone(ZoneId.systemDefault()).toLocalDate())
                        .collect(Collectors.toSet());

        int diasConsecutivos = 0;
        LocalDate dia = LocalDate.now();
        while (diasComMeditacao.contains(dia)) {
            diasConsecutivos++;
            dia = dia.minusDays(1);
        }

        return new SequenciaMeditacaoResponseDTO(diasConsecutivos);
    }

    private SessaoMeditacao buscarSessaoDoUsuario(Long usuarioId, Long sessaoId) {
        return sessaoMeditacaoRepository
                .findByIdAndUsuarioId(sessaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de meditação não encontrada"));
    }
}
