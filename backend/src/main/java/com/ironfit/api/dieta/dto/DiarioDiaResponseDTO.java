package com.ironfit.api.dieta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DiarioDiaResponseDTO(
        LocalDate data,
        Long dietaId,
        String dietaNome,
        Integer metaCaloriasKcal,
        Integer metaProteinaG,
        Integer metaCarboidratoG,
        Integer metaGorduraG,
        BigDecimal caloriasConsumidas,
        BigDecimal proteinaConsumidaG,
        BigDecimal carboidratoConsumidoG,
        BigDecimal gorduraConsumidaG,
        List<DiarioRefeicaoDTO> refeicoes) {}
