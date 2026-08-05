package com.ironfit.api.progresso.mapper;

import com.ironfit.api.progresso.dto.RegistroProgressoResponseDTO;
import com.ironfit.api.progresso.entity.RegistroProgresso;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

/** Converte {@link RegistroProgresso} em DTO, calculando IMC (a partir da altura do Perfil) e massa magra. */
@Component
public class RegistroProgressoMapper {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    public RegistroProgressoResponseDTO toResponseDTO(RegistroProgresso registro, BigDecimal alturaCm) {
        return new RegistroProgressoResponseDTO(
                registro.getId(),
                registro.getDataRegistro(),
                registro.getPesoKg(),
                calcularImc(registro.getPesoKg(), alturaCm),
                registro.getPercentualGordura(),
                calcularMassaMagra(registro.getPesoKg(), registro.getPercentualGordura()),
                registro.getCircunferenciaCinturaCm(),
                registro.getCircunferenciaQuadrilCm(),
                registro.getCircunferenciaBracoCm(),
                registro.getCircunferenciaCoxaCm(),
                registro.getCircunferenciaPeitoCm(),
                registro.getFotoUrl(),
                registro.getObservacoes());
    }

    private BigDecimal calcularImc(BigDecimal pesoKg, BigDecimal alturaCm) {
        if (pesoKg == null || alturaCm == null || alturaCm.signum() <= 0) {
            return null;
        }
        BigDecimal alturaM = alturaCm.divide(CEM, 4, RoundingMode.HALF_UP);
        BigDecimal alturaMQuadrado = alturaM.multiply(alturaM);
        return pesoKg.divide(alturaMQuadrado, 1, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularMassaMagra(BigDecimal pesoKg, BigDecimal percentualGordura) {
        if (pesoKg == null || percentualGordura == null) {
            return null;
        }
        BigDecimal fracaoMagra = BigDecimal.ONE.subtract(percentualGordura.divide(CEM, 4, RoundingMode.HALF_UP));
        return pesoKg.multiply(fracaoMagra).setScale(1, RoundingMode.HALF_UP);
    }
}
