package com.ironfit.api.hidratacao.dto;

import java.time.LocalDate;
import java.util.List;

public record ResumoDiaHidratacaoResponseDTO(
        LocalDate data,
        Integer metaMl,
        Integer totalMl,
        Integer percentual,
        List<RegistroHidratacaoResponseDTO> registros) {}
