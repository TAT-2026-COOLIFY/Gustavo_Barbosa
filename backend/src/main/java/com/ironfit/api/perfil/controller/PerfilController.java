package com.ironfit.api.perfil.controller;

import com.ironfit.api.perfil.dto.AtualizarPerfilRequestDTO;
import com.ironfit.api.perfil.dto.PerfilResponseDTO;
import com.ironfit.api.perfil.service.PerfilService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Perfil")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @GetMapping
    public PerfilResponseDTO obter(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return perfilService.obterPerfil(userDetails.getUsuarioId());
    }

    @PutMapping
    public PerfilResponseDTO atualizar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody AtualizarPerfilRequestDTO request) {
        return perfilService.atualizarPerfil(userDetails.getUsuarioId(), request);
    }

    @PostMapping("/foto")
    public Map<String, String> atualizarFoto(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @RequestParam("arquivo") MultipartFile arquivo) {
        String url = perfilService.atualizarFoto(userDetails.getUsuarioId(), arquivo);
        return Map.of("fotoPerfilUrl", url);
    }
}
