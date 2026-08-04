package com.ironfit.api.dieta.controller;

import com.ironfit.api.dieta.dto.DiarioDiaResponseDTO;
import com.ironfit.api.dieta.dto.DiarioHistoricoDiaDTO;
import com.ironfit.api.dieta.dto.DiarioRefeicaoDTO;
import com.ironfit.api.dieta.service.DiarioService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Diário Alimentar")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/diario")
public class DiarioController {

    private final DiarioService diarioService;

    public DiarioController(DiarioService diarioService) {
        this.diarioService = diarioService;
    }

    @GetMapping
    public DiarioDiaResponseDTO obterDia(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return diarioService.obterDia(userDetails.getUsuarioId(), dataOuHoje(data));
    }

    @PostMapping("/refeicoes/{refeicaoId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DiarioRefeicaoDTO registrarConsumo(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long refeicaoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return diarioService.registrarConsumo(userDetails.getUsuarioId(), refeicaoId, dataOuHoje(data));
    }

    @DeleteMapping("/refeicoes/{refeicaoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerConsumo(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long refeicaoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        diarioService.removerConsumo(userDetails.getUsuarioId(), refeicaoId, dataOuHoje(data));
    }

    @GetMapping("/historico")
    public List<DiarioHistoricoDiaDTO> historico(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @RequestParam(required = false, defaultValue = "7") int dias) {
        return diarioService.historico(userDetails.getUsuarioId(), dias);
    }

    private LocalDate dataOuHoje(LocalDate data) {
        return data != null ? data : LocalDate.now();
    }
}
