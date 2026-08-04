package com.ironfit.api.dieta.dto;

import com.ironfit.api.dieta.entity.NivelAtividade;
import com.ironfit.api.dieta.entity.OrigemDieta;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.math.BigDecimal;
import java.util.List;

public record DietaResponseDTO(
        Long id,
        String nome,
        OrigemDieta origem,
        boolean ativa,
        boolean favorito,
        BigDecimal pesoReferenciaKg,
        NivelAtividade nivelAtividade,
        TipoObjetivo tipoObjetivoReferencia,
        boolean calculoAutomatico,
        Integer metaCaloriasKcal,
        Integer metaProteinaG,
        Integer metaCarboidratoG,
        Integer metaGorduraG,
        Integer metaAguaMl,
        List<RefeicaoResponseDTO> refeicoes) {}
