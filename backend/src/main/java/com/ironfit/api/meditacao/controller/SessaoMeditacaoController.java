package com.ironfit.api.meditacao.controller;

import com.ironfit.api.meditacao.dto.ConcluirMeditacaoRequestDTO;
import com.ironfit.api.meditacao.dto.SessaoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.service.ExecucaoMeditacaoService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sessões de Meditação")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sessoes-meditacao")
public class SessaoMeditacaoController {

    private final ExecucaoMeditacaoService execucaoMeditacaoService;

    public SessaoMeditacaoController(ExecucaoMeditacaoService execucaoMeditacaoService) {
        this.execucaoMeditacaoService = execucaoMeditacaoService;
    }

    @GetMapping("/{id}")
    public SessaoMeditacaoResponseDTO obter(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return execucaoMeditacaoService.obter(userDetails.getUsuarioId(), id);
    }

    @PutMapping("/{id}/concluir")
    public SessaoMeditacaoResponseDTO concluir(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ConcluirMeditacaoRequestDTO request) {
        return execucaoMeditacaoService.concluir(userDetails.getUsuarioId(), id, request);
    }
}
