package com.ironfit.api.progresso.controller;

import com.ironfit.api.progresso.dto.RegistrarProgressoRequestDTO;
import com.ironfit.api.progresso.dto.RegistroProgressoResponseDTO;
import com.ironfit.api.progresso.service.ProgressoService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Progresso")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/progresso")
public class ProgressoController {

    private final ProgressoService progressoService;

    public ProgressoController(ProgressoService progressoService) {
        this.progressoService = progressoService;
    }

    @GetMapping
    public List<RegistroProgressoResponseDTO> listar(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return progressoService.listar(userDetails.getUsuarioId());
    }

    @GetMapping("/ultimo")
    public RegistroProgressoResponseDTO obterUltimo(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return progressoService.obterUltimo(userDetails.getUsuarioId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroProgressoResponseDTO registrar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody RegistrarProgressoRequestDTO request) {
        return progressoService.registrar(userDetails.getUsuarioId(), request);
    }

    @PutMapping("/{id}")
    public RegistroProgressoResponseDTO atualizar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody RegistrarProgressoRequestDTO request) {
        return progressoService.atualizar(userDetails.getUsuarioId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        progressoService.removerRegistro(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/{id}/foto")
    public Map<String, String> atualizarFoto(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        String url = progressoService.atualizarFoto(userDetails.getUsuarioId(), id, arquivo);
        return Map.of("fotoUrl", url);
    }
}
