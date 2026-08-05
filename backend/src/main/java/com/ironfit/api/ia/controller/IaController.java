package com.ironfit.api.ia.controller;

import com.ironfit.api.dieta.dto.DietaRequestDTO;
import com.ironfit.api.dieta.dto.DietaResponseDTO;
import com.ironfit.api.ia.dto.ConversaDetalheDTO;
import com.ironfit.api.ia.dto.ConversaResumoDTO;
import com.ironfit.api.ia.dto.EnviarMensagemRequestDTO;
import com.ironfit.api.ia.dto.EnviarMensagemResponseDTO;
import com.ironfit.api.ia.service.IaChatService;
import com.ironfit.api.security.UsuarioUserDetails;
import com.ironfit.api.treino.dto.TreinoRequestDTO;
import com.ironfit.api.treino.dto.TreinoResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "IA")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final IaChatService iaChatService;

    public IaController(IaChatService iaChatService) {
        this.iaChatService = iaChatService;
    }

    @PostMapping("/mensagens")
    public EnviarMensagemResponseDTO enviarMensagem(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody EnviarMensagemRequestDTO request) {
        return iaChatService.enviarMensagem(userDetails.getUsuarioId(), request);
    }

    @GetMapping("/conversas")
    public List<ConversaResumoDTO> listarConversas(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return iaChatService.listarConversas(userDetails.getUsuarioId());
    }

    @GetMapping("/conversas/{id}")
    public ConversaDetalheDTO obterConversa(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return iaChatService.obterConversa(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/rascunhos/{id}/confirmar-treino")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoResponseDTO confirmarTreino(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TreinoRequestDTO request) {
        return iaChatService.confirmarTreino(userDetails.getUsuarioId(), id, request);
    }

    @PostMapping("/rascunhos/{id}/confirmar-dieta")
    @ResponseStatus(HttpStatus.CREATED)
    public DietaResponseDTO confirmarDieta(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody DietaRequestDTO request) {
        return iaChatService.confirmarDieta(userDetails.getUsuarioId(), id, request);
    }

    @DeleteMapping("/rascunhos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void descartarRascunho(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        iaChatService.descartarRascunho(userDetails.getUsuarioId(), id);
    }
}
