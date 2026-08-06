package com.ironfit.api.dashboard.dto;

import com.ironfit.api.perfil.dto.ObjetivoResponseDTO;
import java.util.List;

public record DashboardResponseDTO(
        ResumoImcDTO imc,
        ObjetivoResponseDTO objetivoAtivo,
        ResumoAguaDashboardDTO agua,
        ResumoTreinoDashboardDTO treino,
        ResumoDietaDashboardDTO dieta,
        ResumoMeditacaoDashboardDTO meditacao,
        long notificacoesNaoLidas,
        boolean rascunhoIaPendente,
        List<ConquistaDTO> conquistas) {}
