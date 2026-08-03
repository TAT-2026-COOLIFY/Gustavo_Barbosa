package com.ironfit.api.treino.controller;

import com.ironfit.api.common.FavoritoTipo;
import com.ironfit.api.common.service.FavoritoService;
import com.ironfit.api.security.UsuarioUserDetails;
import com.ironfit.api.treino.dto.HistoricoSessaoResponseDTO;
import com.ironfit.api.treino.dto.SessaoTreinoResponseDTO;
import com.ironfit.api.treino.dto.TreinoRequestDTO;
import com.ironfit.api.treino.dto.TreinoResponseDTO;
import com.ironfit.api.treino.dto.TreinoResumoDTO;
import com.ironfit.api.treino.dto.VolumeSemanalResponseDTO;
import com.ironfit.api.treino.service.ExecucaoTreinoService;
import com.ironfit.api.treino.service.TreinoService;
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

@Tag(name = "Treinos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;
    private final FavoritoService favoritoService;
    private final ExecucaoTreinoService execucaoTreinoService;

    public TreinoController(
            TreinoService treinoService, FavoritoService favoritoService, ExecucaoTreinoService execucaoTreinoService) {
        this.treinoService = treinoService;
        this.favoritoService = favoritoService;
        this.execucaoTreinoService = execucaoTreinoService;
    }

    @GetMapping
    public List<TreinoResumoDTO> listar(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return treinoService.listarMeusTreinos(userDetails.getUsuarioId());
    }

    @GetMapping("/templates")
    public List<TreinoResumoDTO> listarTemplates(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return treinoService.listarTemplates(userDetails.getUsuarioId());
    }

    @GetMapping("/{id}")
    public TreinoResponseDTO obter(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return treinoService.obterDetalhe(userDetails.getUsuarioId(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoResponseDTO criar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @Valid @RequestBody TreinoRequestDTO request) {
        return treinoService.criar(userDetails.getUsuarioId(), request);
    }

    @PutMapping("/{id}")
    public TreinoResponseDTO atualizar(
            @AuthenticationPrincipal UsuarioUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TreinoRequestDTO request) {
        return treinoService.atualizar(userDetails.getUsuarioId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        treinoService.excluir(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/{id}/duplicar")
    @ResponseStatus(HttpStatus.CREATED)
    public TreinoResponseDTO duplicar(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return treinoService.duplicar(userDetails.getUsuarioId(), id);
    }

    @PostMapping("/{id}/favorito")
    public Map<String, Boolean> alternarFavorito(
            @AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        boolean favorito = favoritoService.alternar(userDetails.getUsuarioId(), FavoritoTipo.TREINO, id);
        return Map.of("favorito", favorito);
    }

    @PostMapping("/{id}/iniciar")
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoTreinoResponseDTO iniciar(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return execucaoTreinoService.iniciar(userDetails.getUsuarioId(), id);
    }

    @GetMapping("/historico")
    public List<HistoricoSessaoResponseDTO> historico(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return execucaoTreinoService.historico(userDetails.getUsuarioId());
    }

    @GetMapping("/volume-semanal")
    public VolumeSemanalResponseDTO volumeSemanal(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return execucaoTreinoService.volumeSemanal(userDetails.getUsuarioId());
    }
}
