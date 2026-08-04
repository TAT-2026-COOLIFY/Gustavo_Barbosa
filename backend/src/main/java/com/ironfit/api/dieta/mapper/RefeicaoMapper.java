package com.ironfit.api.dieta.mapper;

import com.ironfit.api.dieta.dto.RefeicaoAlimentoResponseDTO;
import com.ironfit.api.dieta.dto.RefeicaoResponseDTO;
import com.ironfit.api.dieta.entity.Refeicao;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RefeicaoMapper {

    public RefeicaoResponseDTO toResponseDTO(Refeicao refeicao, List<RefeicaoAlimentoResponseDTO> alimentos) {
        return new RefeicaoResponseDTO(
                refeicao.getId(),
                refeicao.getNome(),
                refeicao.getOrdem(),
                refeicao.getHorarioSugerido(),
                somar(alimentos, RefeicaoAlimentoResponseDTO::calorias),
                somar(alimentos, RefeicaoAlimentoResponseDTO::proteinaG),
                somar(alimentos, RefeicaoAlimentoResponseDTO::carboidratoG),
                somar(alimentos, RefeicaoAlimentoResponseDTO::gorduraG),
                alimentos);
    }

    private BigDecimal somar(
            List<RefeicaoAlimentoResponseDTO> alimentos,
            java.util.function.Function<RefeicaoAlimentoResponseDTO, BigDecimal> extrator) {
        return alimentos.stream().map(extrator).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
