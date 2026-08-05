package com.ironfit.api.hidratacao.dto;

import java.time.LocalDate;

public record HistoricoDiaHidratacaoDTO(LocalDate data, Integer metaMl, Integer totalMl, boolean atingiu) {}
