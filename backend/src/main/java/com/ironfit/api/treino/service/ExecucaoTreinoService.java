package com.ironfit.api.treino.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import com.ironfit.api.treino.dto.ConcluirSessaoRequestDTO;
import com.ironfit.api.treino.dto.HistoricoSessaoResponseDTO;
import com.ironfit.api.treino.dto.RegistrarSerieRequestDTO;
import com.ironfit.api.treino.dto.SerieExecutadaResponseDTO;
import com.ironfit.api.treino.dto.SessaoTreinoResponseDTO;
import com.ironfit.api.treino.dto.VolumeSemanalResponseDTO;
import com.ironfit.api.treino.entity.SerieExecutada;
import com.ironfit.api.treino.entity.SessaoTreino;
import com.ironfit.api.treino.entity.Treino;
import com.ironfit.api.treino.entity.TreinoExercicio;
import com.ironfit.api.treino.mapper.TreinoExercicioMapper;
import com.ironfit.api.treino.repository.SerieExecutadaRepository;
import com.ironfit.api.treino.repository.SessaoTreinoRepository;
import com.ironfit.api.treino.repository.TreinoExercicioRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExecucaoTreinoService {

    private final SessaoTreinoRepository sessaoTreinoRepository;
    private final SerieExecutadaRepository serieExecutadaRepository;
    private final TreinoExercicioRepository treinoExercicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TreinoService treinoService;
    private final TreinoExercicioMapper treinoExercicioMapper;

    public ExecucaoTreinoService(
            SessaoTreinoRepository sessaoTreinoRepository,
            SerieExecutadaRepository serieExecutadaRepository,
            TreinoExercicioRepository treinoExercicioRepository,
            UsuarioRepository usuarioRepository,
            TreinoService treinoService,
            TreinoExercicioMapper treinoExercicioMapper) {
        this.sessaoTreinoRepository = sessaoTreinoRepository;
        this.serieExecutadaRepository = serieExecutadaRepository;
        this.treinoExercicioRepository = treinoExercicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.treinoService = treinoService;
        this.treinoExercicioMapper = treinoExercicioMapper;
    }

    @Transactional
    public SessaoTreinoResponseDTO iniciar(Long usuarioId, Long treinoId) {
        Treino treino = treinoService.buscarAcessivel(usuarioId, treinoId);
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        SessaoTreino sessao = SessaoTreino.builder()
                .usuario(usuario)
                .treino(treino)
                .dataExecucao(Instant.now())
                .concluido(false)
                .build();
        sessaoTreinoRepository.save(sessao);

        return montarResponse(sessao, treino);
    }

    @Transactional(readOnly = true)
    public SessaoTreinoResponseDTO obter(Long usuarioId, Long sessaoTreinoId) {
        SessaoTreino sessao = buscarSessaoDoUsuario(usuarioId, sessaoTreinoId);
        return montarResponse(sessao, sessao.getTreino());
    }

    @Transactional
    public SerieExecutadaResponseDTO registrarSerie(Long usuarioId, Long sessaoTreinoId, RegistrarSerieRequestDTO request) {
        SessaoTreino sessao = buscarSessaoDoUsuario(usuarioId, sessaoTreinoId);

        TreinoExercicio treinoExercicio = treinoExercicioRepository
                .findById(request.treinoExercicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício do treino não encontrado"));

        if (!treinoExercicio.getTreino().getId().equals(sessao.getTreino().getId())) {
            throw new UnauthorizedException("Exercício não pertence ao treino desta sessão");
        }

        SerieExecutada serie = SerieExecutada.builder()
                .sessaoTreino(sessao)
                .treinoExercicio(treinoExercicio)
                .numeroSerie(request.numeroSerie())
                .repeticoesRealizadas(request.repeticoesRealizadas())
                .cargaRealizadaKg(request.cargaRealizadaKg())
                .build();
        serieExecutadaRepository.save(serie);

        return new SerieExecutadaResponseDTO(
                serie.getId(),
                treinoExercicio.getId(),
                serie.getNumeroSerie(),
                serie.getRepeticoesRealizadas(),
                serie.getCargaRealizadaKg());
    }

    @Transactional
    public SessaoTreinoResponseDTO concluir(Long usuarioId, Long sessaoTreinoId, ConcluirSessaoRequestDTO request) {
        SessaoTreino sessao = buscarSessaoDoUsuario(usuarioId, sessaoTreinoId);
        sessao.setConcluido(true);
        sessao.setDuracaoSegundos(request.duracaoSegundos());
        sessaoTreinoRepository.save(sessao);
        return montarResponse(sessao, sessao.getTreino());
    }

    @Transactional(readOnly = true)
    public List<HistoricoSessaoResponseDTO> historico(Long usuarioId) {
        return sessaoTreinoRepository.findByUsuarioIdOrderByDataExecucaoDesc(usuarioId).stream()
                .map(sessao -> new HistoricoSessaoResponseDTO(
                        sessao.getId(),
                        sessao.getTreino().getNome(),
                        sessao.getDataExecucao(),
                        sessao.getDuracaoSegundos(),
                        sessao.isConcluido(),
                        serieExecutadaRepository.findBySessaoTreinoId(sessao.getId()).size()))
                .toList();
    }

    public VolumeSemanalResponseDTO volumeSemanal(Long usuarioId) {
        Instant desde = Instant.now().minus(7, ChronoUnit.DAYS);
        BigDecimal volume = serieExecutadaRepository.calcularVolumeDesde(usuarioId, desde);
        return new VolumeSemanalResponseDTO(volume);
    }

    private SessaoTreino buscarSessaoDoUsuario(Long usuarioId, Long sessaoTreinoId) {
        return sessaoTreinoRepository
                .findByIdAndUsuarioId(sessaoTreinoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de treino não encontrada"));
    }

    private SessaoTreinoResponseDTO montarResponse(SessaoTreino sessao, Treino treino) {
        var exercicios = treinoExercicioRepository.findByTreinoIdOrderByOrdem(treino.getId()).stream()
                .map(treinoExercicioMapper::toResponseDTO)
                .toList();

        return new SessaoTreinoResponseDTO(
                sessao.getId(),
                treino.getId(),
                treino.getNome(),
                sessao.getDataExecucao(),
                sessao.getDuracaoSegundos(),
                sessao.isConcluido(),
                exercicios);
    }
}
