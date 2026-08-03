package com.ironfit.api.perfil.controller;

import com.ironfit.api.perfil.dto.CriarObjetivoRequestDTO;
import com.ironfit.api.perfil.dto.ObjetivoResponseDTO;
import com.ironfit.api.perfil.service.ObjetivoService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Objetivos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/objetivos")
public class ObjetivoController {

    private final ObjetivoService objetivoService;

    public ObjetivoController(ObjetivoService objetivoService) {
        this.objetivoService = objetivoService;
    }

    @GetMapping
    public List<ObjetivoResponseDTO> listar(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return objetivoService.listar(userDetails.getUsuarioId());
    }

    @GetMapping("/ativo")
    public ObjetivoResponseDTO obterAtivo(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return objetivoService.obterAtivo(userDetails.getUsuarioId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObjetivoResponseDTO criar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody CriarObjetivoRequestDTO request) {
        return objetivoService.criar(userDetails.getUsuarioId(), request);
    }

    @PutMapping("/{id}")
    public ObjetivoResponseDTO atualizar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CriarObjetivoRequestDTO request) {
        return objetivoService.atualizar(userDetails.getUsuarioId(), id, request);
    }
}
