package com.ironfit.api.dieta.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.dieta.dto.AlimentoRequestDTO;
import com.ironfit.api.dieta.dto.AlimentoResponseDTO;
import com.ironfit.api.dieta.entity.Alimento;
import com.ironfit.api.dieta.entity.CategoriaAlimento;
import com.ironfit.api.dieta.mapper.AlimentoMapper;
import com.ironfit.api.dieta.repository.AlimentoRepository;
import com.ironfit.api.dieta.repository.RefeicaoAlimentoRepository;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlimentoService {

    private final AlimentoRepository alimentoRepository;
    private final RefeicaoAlimentoRepository refeicaoAlimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlimentoMapper alimentoMapper;

    public AlimentoService(
            AlimentoRepository alimentoRepository,
            RefeicaoAlimentoRepository refeicaoAlimentoRepository,
            UsuarioRepository usuarioRepository,
            AlimentoMapper alimentoMapper) {
        this.alimentoRepository = alimentoRepository;
        this.refeicaoAlimentoRepository = refeicaoAlimentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.alimentoMapper = alimentoMapper;
    }

    public List<AlimentoResponseDTO> buscar(Long usuarioId, String q, CategoriaAlimento categoria) {
        String termo = (q == null || q.isBlank()) ? null : q.trim();
        return alimentoRepository.buscar(usuarioId, termo, categoria).stream()
                .map(alimentoMapper::toResponseDTO)
                .toList();
    }

    public AlimentoResponseDTO obter(Long id) {
        return alimentoMapper.toResponseDTO(buscarPorId(id));
    }

    @Transactional
    public AlimentoResponseDTO criar(Long usuarioId, AlimentoRequestDTO request) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Alimento alimento = Alimento.builder()
                .usuario(usuario)
                .nome(request.nome())
                .marca(request.marca())
                .categoria(request.categoria())
                .caloriasPor100g(request.caloriasPor100g())
                .proteinaPor100g(request.proteinaPor100g())
                .carboidratoPor100g(request.carboidratoPor100g())
                .gorduraPor100g(request.gorduraPor100g())
                .fibraPor100g(request.fibraPor100g())
                .build();

        alimentoRepository.save(alimento);
        return alimentoMapper.toResponseDTO(alimento);
    }

    @Transactional
    public AlimentoResponseDTO atualizar(Long usuarioId, Long id, AlimentoRequestDTO request) {
        Alimento alimento = alimentoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento personalizado não encontrado"));

        alimento.setNome(request.nome());
        alimento.setMarca(request.marca());
        alimento.setCategoria(request.categoria());
        alimento.setCaloriasPor100g(request.caloriasPor100g());
        alimento.setProteinaPor100g(request.proteinaPor100g());
        alimento.setCarboidratoPor100g(request.carboidratoPor100g());
        alimento.setGorduraPor100g(request.gorduraPor100g());
        alimento.setFibraPor100g(request.fibraPor100g());

        alimentoRepository.save(alimento);
        return alimentoMapper.toResponseDTO(alimento);
    }

    @Transactional
    public void excluir(Long usuarioId, Long id) {
        Alimento alimento = alimentoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Alimento personalizado não encontrado"));

        if (refeicaoAlimentoRepository.existsByAlimentoId(id)) {
            throw new BusinessException("Alimento está em uso em uma ou mais refeições e não pode ser excluído");
        }

        alimentoRepository.delete(alimento);
    }

    private Alimento buscarPorId(Long id) {
        return alimentoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alimento não encontrado"));
    }
}
