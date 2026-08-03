package com.ironfit.api.treino.controller;

import com.ironfit.api.security.UsuarioUserDetails;
import com.ironfit.api.treino.dto.ConcluirSessaoRequestDTO;
import com.ironfit.api.treino.dto.RegistrarSerieRequestDTO;
import com.ironfit.api.treino.dto.SerieExecutadaResponseDTO;
import com.ironfit.api.treino.dto.SessaoTreinoResponseDTO;
import com.ironfit.api.treino.service.ExecucaoTreinoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Sessões de Treino")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sessoes-treino")
public class SessaoTreinoController {

    private final ExecucaoTreinoService execucaoTreinoService;

    public SessaoTreinoController(ExecucaoTreinoService execucaoTreinoService) {
        this.execucaoTreinoService = execucaoTreinoService;
    }

    @GetMapping("/{id}")
    public SessaoTreinoResponseDTO obter(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return execucaoTreinoService.obter(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/{id}/series")
    @ResponseStatus(HttpStatus.CREATED)
    public SerieExecutadaResponseDTO registrarSerie(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody RegistrarSerieRequestDTO request) {
        return execucaoTreinoService.registrarSerie(userDetails.getUsuarioId(), id, request);
    }

    @PutMapping("/{id}/concluir")
    public SessaoTreinoResponseDTO concluir(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ConcluirSessaoRequestDTO request) {
        return execucaoTreinoService.concluir(userDetails.getUsuarioId(), id, request);
    }
}
