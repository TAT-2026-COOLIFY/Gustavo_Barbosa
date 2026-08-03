package com.ironfit.api.perfil.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.exception.UnauthorizedException;
import com.ironfit.api.perfil.dto.CriarObjetivoRequestDTO;
import com.ironfit.api.perfil.dto.ObjetivoResponseDTO;
import com.ironfit.api.perfil.entity.Objetivo;
import com.ironfit.api.perfil.mapper.ObjetivoMapper;
import com.ironfit.api.perfil.repository.ObjetivoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObjetivoService {

    private final ObjetivoRepository objetivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetivoMapper objetivoMapper;

    public ObjetivoService(
            ObjetivoRepository objetivoRepository, UsuarioRepository usuarioRepository, ObjetivoMapper objetivoMapper) {
        this.objetivoRepository = objetivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objetivoMapper = objetivoMapper;
    }

    public List<ObjetivoResponseDTO> listar(Long usuarioId) {
        return objetivoRepository.findByUsuarioIdOrderByDataInicioDesc(usuarioId).stream()
                .map(objetivoMapper::toResponseDTO)
                .toList();
    }

    public ObjetivoResponseDTO obterAtivo(Long usuarioId) {
        Objetivo ativo = objetivoRepository
                .findByUsuarioIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum objetivo ativo definido"));
        return objetivoMapper.toResponseDTO(ativo);
    }

    @Transactional
    public ObjetivoResponseDTO criar(Long usuarioId, CriarObjetivoRequestDTO request) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        objetivoRepository.desativarTodosDoUsuario(usuarioId);

        Objetivo objetivo = Objetivo.builder()
                .usuario(usuario)
                .tipo(request.tipo())
                .pesoMetaKg(request.pesoMetaKg())
                .dataInicio(LocalDate.now())
                .dataAlvo(request.dataAlvo())
                .ativo(true)
                .build();

        objetivoRepository.save(objetivo);
        return objetivoMapper.toResponseDTO(objetivo);
    }

    @Transactional
    public ObjetivoResponseDTO atualizar(Long usuarioId, Long objetivoId, CriarObjetivoRequestDTO request) {
        Objetivo objetivo = objetivoRepository
                .findById(objetivoId)
                .orElseThrow(() -> new ResourceNotFoundException("Objetivo não encontrado"));

        if (!objetivo.getUsuario().getId().equals(usuarioId)) {
            throw new UnauthorizedException("Objetivo não pertence ao usuário autenticado");
        }

        objetivo.setTipo(request.tipo());
        objetivo.setPesoMetaKg(request.pesoMetaKg());
        objetivo.setDataAlvo(request.dataAlvo());

        objetivoRepository.save(objetivo);
        return objetivoMapper.toResponseDTO(objetivo);
    }
}
