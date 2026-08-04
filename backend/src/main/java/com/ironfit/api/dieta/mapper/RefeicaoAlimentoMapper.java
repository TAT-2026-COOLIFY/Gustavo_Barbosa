package com.ironfit.api.dieta.mapper;

import com.ironfit.api.dieta.dto.RefeicaoAlimentoResponseDTO;
import com.ironfit.api.dieta.entity.Alimento;
import com.ironfit.api.dieta.entity.RefeicaoAlimento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/** Calcula os valores nutricionais de um item de refeição a partir da tabela do alimento (por 100g) × a quantidade informada. */
@Component
public class RefeicaoAlimentoMapper {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    public RefeicaoAlimentoResponseDTO toResponseDTO(RefeicaoAlimento refeicaoAlimento) {
        Alimento alimento = refeicaoAlimento.getAlimento();
        BigDecimal quantidade = refeicaoAlimento.getQuantidadeGramas();

        return new RefeicaoAlimentoResponseDTO(
                refeicaoAlimento.getId(),
                alimento.getId(),
                alimento.getNome(),
                quantidade,
                proporcional(alimento.getCaloriasPor100g(), quantidade),
                proporcional(alimento.getProteinaPor100g(), quantidade),
                proporcional(alimento.getCarboidratoPor100g(), quantidade),
                proporcional(alimento.getGorduraPor100g(), quantidade));
    }

    private BigDecimal proporcional(BigDecimal valorPor100g, BigDecimal quantidadeGramas) {
        return valorPor100g.multiply(quantidadeGramas).divide(CEM, 1, RoundingMode.HALF_UP);
    }
}
