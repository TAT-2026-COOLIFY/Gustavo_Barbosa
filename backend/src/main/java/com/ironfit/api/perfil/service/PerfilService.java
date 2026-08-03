package com.ironfit.api.perfil.service;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.perfil.dto.AtualizarPerfilRequestDTO;
import com.ironfit.api.perfil.dto.PerfilResponseDTO;
import com.ironfit.api.perfil.entity.Perfil;
import com.ironfit.api.perfil.mapper.PerfilMapper;
import com.ironfit.api.perfil.repository.PerfilRepository;
import com.ironfit.api.storage.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PerfilService {

    private static final String SUBPASTA_FOTOS_PERFIL = "perfil";

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilMapper perfilMapper;
    private final FileStorageService fileStorageService;

    public PerfilService(
            PerfilRepository perfilRepository,
            UsuarioRepository usuarioRepository,
            PerfilMapper perfilMapper,
            FileStorageService fileStorageService) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilMapper = perfilMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public PerfilResponseDTO obterPerfil(Long usuarioId) {
        return perfilMapper.toResponseDTO(obterOuCriarPerfil(usuarioId));
    }

    @Transactional
    public PerfilResponseDTO atualizarPerfil(Long usuarioId, AtualizarPerfilRequestDTO request) {
        Perfil perfil = obterOuCriarPerfil(usuarioId);

        perfil.getUsuario().setNome(request.nome());
        perfil.setDataNascimento(request.dataNascimento());
        perfil.setSexo(request.sexo());
        perfil.setAlturaCm(request.alturaCm());
        perfil.setNivelExperiencia(request.nivelExperiencia());
        perfil.setBio(request.bio());

        perfilRepository.save(perfil);
        return perfilMapper.toResponseDTO(perfil);
    }

    @Transactional
    public String atualizarFoto(Long usuarioId, MultipartFile arquivo) {
        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        String url = fileStorageService.salvar(arquivo, SUBPASTA_FOTOS_PERFIL);
        usuario.setFotoPerfilUrl(url);
        usuarioRepository.save(usuario);
        return url;
    }

    private Perfil obterOuCriarPerfil(Long usuarioId) {
        return perfilRepository.findByUsuarioId(usuarioId).orElseGet(() -> {
            Usuario usuario = usuarioRepository
                    .findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            return perfilRepository.save(Perfil.builder().usuario(usuario).build());
        });
    }
}
