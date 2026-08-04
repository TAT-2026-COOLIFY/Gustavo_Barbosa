package com.ironfit.api.dieta.controller;

import com.ironfit.api.common.FavoritoTipo;
import com.ironfit.api.common.service.FavoritoService;
import com.ironfit.api.dieta.dto.CalcularMetasRequestDTO;
import com.ironfit.api.dieta.dto.DietaRequestDTO;
import com.ironfit.api.dieta.dto.DietaResponseDTO;
import com.ironfit.api.dieta.dto.DietaResumoDTO;
import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.dieta.service.CalculoNutricionalService;
import com.ironfit.api.dieta.service.DietaService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dietas")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/dietas")
public class DietaController {

    private final DietaService dietaService;
    private final FavoritoService favoritoService;
    private final CalculoNutricionalService calculoNutricionalService;

    public DietaController(
            DietaService dietaService, FavoritoService favoritoService, CalculoNutricionalService calculoNutricionalService) {
        this.dietaService = dietaService;
        this.favoritoService = favoritoService;
        this.calculoNutricionalService = calculoNutricionalService;
    }

    @GetMapping
    public List<DietaResumoDTO> listar(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return dietaService.listarMinhas(userDetails.getUsuarioId());
    }

    @GetMapping("/templates")
    public List<DietaResumoDTO> listarTemplates(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return dietaService.listarTemplates(userDetails.getUsuarioId());
    }

    @GetMapping("/ativa")
    public DietaResponseDTO obterAtiva(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return dietaService.obterAtiva(userDetails.getUsuarioId());
    }

    @GetMapping("/{id}")
    public DietaResponseDTO obter(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return dietaService.obterDetalhe(userDetails.getUsuarioId(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DietaResponseDTO criar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody DietaRequestDTO request) {
        return dietaService.criar(userDetails.getUsuarioId(), request);
    }

    @PutMapping("/{id}")
    public DietaResponseDTO atualizar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody DietaRequestDTO request) {
        return dietaService.atualizar(userDetails.getUsuarioId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        dietaService.excluir(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/{id}/duplicar")
    @ResponseStatus(HttpStatus.CREATED)
    public DietaResponseDTO duplicar(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return dietaService.duplicar(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/{id}/favorito")
    public Map<String, Boolean> alternarFavorito(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        boolean favorito = favoritoService.alternar(userDetails.getUsuarioId(), FavoritoTipo.DIETA, id);
        return Map.of("favorito", favorito);
    }

    @PostMapping("/{id}/ativar")
    public DietaResponseDTO ativar(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return dietaService.ativar(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/calcular-metas")
    public MetaNutricionalCalculadaDTO calcularMetas(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody CalcularMetasRequestDTO request) {
        return calculoNutricionalService.calcular(
                userDetails.getUsuarioId(), request.pesoReferenciaKg(), request.nivelAtividade(), request.tipoObjetivo());
    }
}
