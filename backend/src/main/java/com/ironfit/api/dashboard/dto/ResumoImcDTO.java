package com.ironfit.api.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResumoImcDTO(BigDecimal pesoKg, BigDecimal imc, BigDecimal massaMagraKg, LocalDate dataRegistro) {}
