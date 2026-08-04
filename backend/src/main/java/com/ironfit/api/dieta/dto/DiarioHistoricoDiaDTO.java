package com.ironfit.api.dieta.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiarioHistoricoDiaDTO(LocalDate data, Integer metaCaloriasKcal, BigDecimal caloriasConsumidas) {}
