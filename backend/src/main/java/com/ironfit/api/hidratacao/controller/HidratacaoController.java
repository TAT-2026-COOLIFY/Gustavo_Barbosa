package com.ironfit.api.hidratacao.controller;

import com.ironfit.api.hidratacao.dto.AtualizarMetaHidratacaoRequestDTO;
import com.ironfit.api.hidratacao.dto.HistoricoDiaHidratacaoDTO;
import com.ironfit.api.hidratacao.dto.MetaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.RegistroHidratacaoRequestDTO;
import com.ironfit.api.hidratacao.dto.ResumoDiaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.SequenciaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.service.HidratacaoService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
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

@Tag(name = "Hidratação")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/hidratacao")
public class HidratacaoController {

    private final HidratacaoService hidratacaoService;

    public HidratacaoController(HidratacaoService hidratacaoService) {
        this.hidratacaoService = hidratacaoService;
    }

    @GetMapping("/meta")
    public MetaHidratacaoResponseDTO obterMeta(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return hidratacaoService.obterMetaAtiva(userDetails.getUsuarioId());
    }

    @PutMapping("/meta")
    public MetaHidratacaoResponseDTO atualizarMeta(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @Valid @RequestBody AtualizarMetaHidratacaoRequestDTO request) {
        return hidratacaoService.atualizarMeta(userDetails.getUsuarioId(), request);
    }

    @GetMapping("/hoje")
    public ResumoDiaHidratacaoResponseDTO obterHoje(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return hidratacaoService.obterResumoDia(userDetails.getUsuarioId(), LocalDate.now());
    }

    @PostMapping("/registros")
    @ResponseStatus(HttpStatus.CREATED)
    public ResumoDiaHidratacaoResponseDTO registrar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody RegistroHidratacaoRequestDTO request) {
        return hidratacaoService.registrar(userDetails.getUsuarioId(), request);
    }

    @DeleteMapping("/registros/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerRegistro(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        hidratacaoService.removerRegistro(userDetails.getUsuarioId(), id);
    }

    @GetMapping("/historico")
    public List<HistoricoDiaHidratacaoDTO> historico(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @RequestParam(defaultValue = "7") int dias) {
        return hidratacaoService.historico(userDetails.getUsuarioId(), dias);
    }

    @GetMapping("/sequencia")
    public SequenciaHidratacaoResponseDTO sequencia(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return hidratacaoService.calcularSequencia(userDetails.getUsuarioId());
    }
}
