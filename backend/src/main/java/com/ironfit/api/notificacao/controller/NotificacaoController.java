package com.ironfit.api.notificacao.controller;

import com.ironfit.api.notificacao.dto.ContadorNaoLidasResponseDTO;
import com.ironfit.api.notificacao.dto.NotificacaoResponseDTO;
import com.ironfit.api.notificacao.service.NotificacaoService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notificações")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping
    public List<NotificacaoResponseDTO> listar(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return notificacaoService.listar(userDetails.getUsuarioId());
    }

    @GetMapping("/nao-lidas")
    public ContadorNaoLidasResponseDTO contarNaoLidas(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return new ContadorNaoLidasResponseDTO(notificacaoService.contarNaoLidas(userDetails.getUsuarioId()));
    }

    @PutMapping("/{id}/lida")
    public NotificacaoResponseDTO marcarLida(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return notificacaoService.marcarLida(userDetails.getUsuarioId(), id);
    }

    @PutMapping("/lidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarTodasComoLidas(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        notificacaoService.marcarTodasComoLidas(userDetails.getUsuarioId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        notificacaoService.remover(userDetails.getUsuarioId(), id);
    }
}
