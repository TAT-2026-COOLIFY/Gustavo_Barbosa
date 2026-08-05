package com.ironfit.api.treino.service;

import com.ironfit.api.common.FavoritoTipo;
import com.ironfit.api.common.service.FavoritoService;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import com.ironfit.api.treino.dto.TreinoExercicioRequestDTO;
import com.ironfit.api.treino.dto.TreinoRequestDTO;
import com.ironfit.api.treino.dto.TreinoResponseDTO;
import com.ironfit.api.treino.dto.TreinoResumoDTO;
import com.ironfit.api.treino.entity.Exercicio;
import com.ironfit.api.treino.entity.OrigemTreino;
import com.ironfit.api.treino.entity.Treino;
import com.ironfit.api.treino.entity.TreinoExercicio;
import com.ironfit.api.treino.mapper.TreinoExercicioMapper;
import com.ironfit.api.treino.mapper.TreinoMapper;
import com.ironfit.api.treino.repository.ExercicioRepository;
import com.ironfit.api.treino.repository.TreinoExercicioRepository;
import com.ironfit.api.treino.repository.TreinoRepository;
import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final TreinoExercicioRepository treinoExercicioRepository;
    private final ExercicioRepository exercicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final TreinoMapper treinoMapper;
    private final TreinoExercicioMapper treinoExercicioMapper;
    private final FavoritoService favoritoService;

    public TreinoService(
            TreinoRepository treinoRepository,
            TreinoExercicioRepository treinoExercicioRepository,
            ExercicioRepository exercicioRepository,
            UsuarioRepository usuarioRepository,
            TreinoMapper treinoMapper,
            TreinoExercicioMapper treinoExercicioMapper,
            FavoritoService favoritoService) {
        this.treinoRepository = treinoRepository;
        this.treinoExercicioRepository = treinoExercicioRepository;
        this.exercicioRepository = exercicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.treinoMapper = treinoMapper;
        this.treinoExercicioMapper = treinoExercicioMapper;
        this.favoritoService = favoritoService;
    }

    public List<TreinoResumoDTO> listarMeusTreinos(Long usuarioId) {
        return treinoRepository.findByUsuarioIdOrderByAtualizadoEmDesc(usuarioId).stream()
                .map(treino -> toResumo(treino, usuarioId))
                .toList();
    }

    public List<TreinoResumoDTO> listarTemplates(Long usuarioId) {
        return treinoRepository.findByUsuarioIsNullAndOrigemOrderByNome(OrigemTreino.TEMPLATE).stream()
                .map(treino -> toResumo(treino, usuarioId))
                .toList();
    }

    @Transactional(readOnly = true)
    public TreinoResponseDTO obterDetalhe(Long usuarioId, Long treinoId) {
        Treino treino = buscarAcessivel(usuarioId, treinoId);
        var exercicios = treinoExercicioRepository.findByTreinoIdOrderByOrdem(treinoId).stream()
                .map(treinoExercicioMapper::toResponseDTO)
                .toList();
        boolean favorito = favoritoService.isFavorito(usuarioId, FavoritoTipo.TREINO, treinoId);
        return treinoMapper.toResponseDTO(treino, favorito, exercicios);
    }

    @Transactional
    public TreinoResponseDTO criar(Long usuarioId, TreinoRequestDTO request) {
        return criar(usuarioId, request, OrigemTreino.MANUAL);
    }

    /** Usado pelo módulo de IA para criar um treino já marcado com {@link OrigemTreino#IA} ao confirmar um rascunho. */
    @Transactional
    public TreinoResponseDTO criar(Long usuarioId, TreinoRequestDTO request, OrigemTreino origem) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Treino treino = Treino.builder()
                .usuario(usuario)
                .nome(request.nome())
                .origem(origem)
                .build();
        treinoRepository.save(treino);

        var exercicios = salvarExercicios(treino, request.exercicios());
        return treinoMapper.toResponseDTO(treino, false, exercicios);
    }

    @Transactional
    public TreinoResponseDTO atualizar(Long usuarioId, Long treinoId, TreinoRequestDTO request) {
        Treino treino = treinoRepository
                .findByIdAndUsuarioId(treinoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado"));

        treino.setNome(request.nome());
        treinoRepository.save(treino);

        treinoExercicioRepository.deleteByTreinoId(treinoId);
        treinoExercicioRepository.flush();
        var exercicios = salvarExercicios(treino, request.exercicios());

        boolean favorito = favoritoService.isFavorito(usuarioId, FavoritoTipo.TREINO, treinoId);
        return treinoMapper.toResponseDTO(treino, favorito, exercicios);
    }

    @Transactional
    public void excluir(Long usuarioId, Long treinoId) {
        Treino treino = treinoRepository
                .findByIdAndUsuarioId(treinoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado"));
        treinoRepository.delete(treino);
    }

    @Transactional
    public TreinoResponseDTO duplicar(Long usuarioId, Long treinoId) {
        Treino origem = buscarAcessivel(usuarioId, treinoId);
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Treino copia = Treino.builder()
                .usuario(usuario)
                .nome(origem.getNome() + " (cópia)")
                .origem(OrigemTreino.MANUAL)
                .templateOrigemId(origem.getId())
                .build();
        treinoRepository.save(copia);

        var exerciciosOrigem = treinoExercicioRepository.findByTreinoIdOrderByOrdem(treinoId);
        var requests = exerciciosOrigem.stream()
                .map(te -> new TreinoExercicioRequestDTO(
                        te.getExercicio().getId(),
                        te.getOrdem(),
                        te.getSeriesAlvo(),
                        te.getRepeticoesAlvo(),
                        te.getCargaAlvoKg(),
                        te.getDescansoSegundos()))
                .toList();

        var exercicios = salvarExercicios(copia, requests);
        return treinoMapper.toResponseDTO(copia, false, exercicios);
    }

    /** Retorna o treino se pertencer ao usuário ou for um treino-modelo do sistema. */
    Treino buscarAcessivel(Long usuarioId, Long treinoId) {
        Treino treino =
                treinoRepository.findById(treinoId).orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado"));

        boolean ehDoUsuario = treino.getUsuario() != null && treino.getUsuario().getId().equals(usuarioId);
        boolean ehTemplate = treino.getUsuario() == null && treino.getOrigem() == OrigemTreino.TEMPLATE;

        if (!ehDoUsuario && !ehTemplate) {
            throw new UnauthorizedException("Treino não acessível para este usuário");
        }
        return treino;
    }

    private List<com.ironfit.api.treino.dto.TreinoExercicioResponseDTO> salvarExercicios(
            Treino treino, List<TreinoExercicioRequestDTO> requests) {
        return requests.stream()
                .map(req -> {
                    Exercicio exercicio = exercicioRepository
                            .findById(req.exercicioId())
                            .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado: " + req.exercicioId()));

                    TreinoExercicio treinoExercicio = TreinoExercicio.builder()
                            .treino(treino)
                            .exercicio(exercicio)
                            .ordem(req.ordem())
                            .seriesAlvo(req.seriesAlvo())
                            .repeticoesAlvo(req.repeticoesAlvo())
                            .cargaAlvoKg(req.cargaAlvoKg())
                            .descansoSegundos(req.descansoSegundos())
                            .build();

                    treinoExercicioRepository.save(treinoExercicio);
                    return treinoExercicioMapper.toResponseDTO(treinoExercicio);
                })
                .toList();
    }

    private TreinoResumoDTO toResumo(Treino treino, Long usuarioId) {
        int quantidade = treinoExercicioRepository.findByTreinoIdOrderByOrdem(treino.getId()).size();
        boolean favorito = favoritoService.isFavorito(usuarioId, FavoritoTipo.TREINO, treino.getId());
        return treinoMapper.toResumoDTO(treino, favorito, quantidade);
    }
}
