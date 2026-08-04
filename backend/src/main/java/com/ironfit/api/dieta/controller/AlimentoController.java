package com.ironfit.api.dieta.controller;

import com.ironfit.api.dieta.dto.AlimentoRequestDTO;
import com.ironfit.api.dieta.dto.AlimentoResponseDTO;
import com.ironfit.api.dieta.entity.CategoriaAlimento;
import com.ironfit.api.dieta.service.AlimentoService;
import com.ironfit.api.security.UsuarioUserDetails;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Alimentos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/alimentos")
public class AlimentoController {

    private final AlimentoService alimentoService;

    public AlimentoController(AlimentoService alimentoService) {
        this.alimentoService = alimentoService;
    }

    @GetMapping
    public List<AlimentoResponseDTO> buscar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) CategoriaAlimento categoria) {
        return alimentoService.buscar(userDetails.getUsuarioId(), q, categoria);
    }

    @GetMapping("/{id}")
    public AlimentoResponseDTO obter(@PathVariable Long id) {
        return alimentoService.obter(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlimentoResponseDTO criar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody AlimentoRequestDTO request) {
        return alimentoService.criar(userDetails.getUsuarioId(), request);
    }

    @PutMapping("/{id}")
    public AlimentoResponseDTO atualizar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AlimentoRequestDTO request) {
        return alimentoService.atualizar(userDetails.getUsuarioId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        alimentoService.excluir(userDetails.getUsuarioId(), id);
    }
}
