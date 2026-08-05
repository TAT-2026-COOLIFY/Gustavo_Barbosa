package com.ironfit.api.meditacao.controller;

import com.ironfit.api.meditacao.dto.HistoricoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.dto.MeditacaoResponseDTO;
import com.ironfit.api.meditacao.dto.SequenciaMeditacaoResponseDTO;
import com.ironfit.api.meditacao.dto.SessaoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.entity.TipoMeditacao;
import com.ironfit.api.meditacao.service.ExecucaoMeditacaoService;
import com.ironfit.api.meditacao.service.MeditacaoService;
import com.ironfit.api.security.UsuarioUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Meditação")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/meditacoes")
public class MeditacaoController {

    private final MeditacaoService meditacaoService;
    private final ExecucaoMeditacaoService execucaoMeditacaoService;

    public MeditacaoController(MeditacaoService meditacaoService, ExecucaoMeditacaoService execucaoMeditacaoService) {
        this.meditacaoService = meditacaoService;
        this.execucaoMeditacaoService = execucaoMeditacaoService;
    }

    @GetMapping
    public List<MeditacaoResponseDTO> listar(@RequestParam(required = false) TipoMeditacao tipo) {
        return meditacaoService.listar(tipo);
    }

    @GetMapping("/{id}")
    public MeditacaoResponseDTO obter(@PathVariable Long id) {
        return meditacaoService.obter(id);
    }

    @PostMapping("/{id}/iniciar")
    public SessaoMeditacaoResponseDTO iniciar(@AuthenticationPrincipal UsuarioUserDetails userDetails, @PathVariable Long id) {
        return execucaoMeditacaoService.iniciar(userDetails.getUsuarioId(), id);
    }

    @GetMapping("/historico")
    public List<HistoricoMeditacaoResponseDTO> historico(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return execucaoMeditacaoService.historico(userDetails.getUsuarioId());
    }

    @GetMapping("/sequencia")
    public SequenciaMeditacaoResponseDTO sequencia(@AuthenticationPrincipal UsuarioUserDetails userDetails) {
        return execucaoMeditacaoService.sequencia(userDetails.getUsuarioId());
    }
}
