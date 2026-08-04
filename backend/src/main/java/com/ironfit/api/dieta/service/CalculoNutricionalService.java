package com.ironfit.api.dieta.service;

import com.ironfit.api.dieta.dto.MetaNutricionalCalculadaDTO;
import com.ironfit.api.dieta.entity.NivelAtividade;
import com.ironfit.api.dieta.service.estrategia.EstrategiaMetaNutricionalFactory;
import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.perfil.dto.PerfilResponseDTO;
import com.ironfit.api.perfil.entity.Sexo;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import com.ironfit.api.perfil.service.PerfilService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import org.springframework.stereotype.Service;

/**
 * Calcula a meta nutricional automática de uma dieta: taxa metabólica basal (TMB, fórmula de Mifflin-St Jeor)
 * ajustada por um fator de atividade física para chegar ao gasto energético total (GET/TDEE), que então é
 * traduzido em metas de calorias e macronutrientes pela {@link EstrategiaMetaNutricionalFactory}.
 */
@Service
public class CalculoNutricionalService {

    private final PerfilService perfilService;
    private final EstrategiaMetaNutricionalFactory estrategiaFactory;

    public CalculoNutricionalService(PerfilService perfilService, EstrategiaMetaNutricionalFactory estrategiaFactory) {
        this.perfilService = perfilService;
        this.estrategiaFactory = estrategiaFactory;
    }

    public MetaNutricionalCalculadaDTO calcular(
            Long usuarioId, BigDecimal pesoReferenciaKg, NivelAtividade nivelAtividade, TipoObjetivo tipoObjetivo) {
        PerfilResponseDTO perfil = perfilService.obterPerfil(usuarioId);
        if (perfil.dataNascimento() == null || perfil.alturaCm() == null || perfil.sexo() == null) {
            throw new BusinessException(
                    "Complete data de nascimento, sexo e altura no seu perfil antes de calcular as metas automaticamente");
        }

        int idade = Period.between(perfil.dataNascimento(), LocalDate.now()).getYears();
        int tmbKcal = calcularTmb(pesoReferenciaKg, perfil.alturaCm(), idade, perfil.sexo());
        int gastoTotalDiarioKcal = (int) Math.round(tmbKcal * fatorAtividade(nivelAtividade));

        return estrategiaFactory.obter(tipoObjetivo).calcular(tmbKcal, gastoTotalDiarioKcal, pesoReferenciaKg);
    }

    /** Fórmula de Mifflin-St Jeor. Para sexo OUTRO, usa a média dos ajustes masculino/feminino como aproximação. */
    private int calcularTmb(BigDecimal pesoKg, BigDecimal alturaCm, int idade, Sexo sexo) {
        double base = 10 * pesoKg.doubleValue() + 6.25 * alturaCm.doubleValue() - 5 * idade;
        double ajuste =
                switch (sexo) {
                    case MASCULINO -> 5;
                    case FEMININO -> -161;
                    case OUTRO -> -78;
                };
        return (int) Math.round(base + ajuste);
    }

    private double fatorAtividade(NivelAtividade nivelAtividade) {
        return switch (nivelAtividade) {
            case SEDENTARIO -> 1.2;
            case LEVE -> 1.375;
            case MODERADO -> 1.55;
            case INTENSO -> 1.725;
            case MUITO_INTENSO -> 1.9;
        };
    }
}
