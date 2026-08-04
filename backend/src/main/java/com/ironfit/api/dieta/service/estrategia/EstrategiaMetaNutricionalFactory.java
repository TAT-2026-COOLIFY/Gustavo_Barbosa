package com.ironfit.api.dieta.service.estrategia;

import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Factory: seleciona a {@link EstrategiaMetaNutricional} adequada para um {@link TipoObjetivo}. */
@Component
public class EstrategiaMetaNutricionalFactory {

    private final Map<TipoObjetivo, EstrategiaMetaNutricional> estrategiasPorTipo;

    public EstrategiaMetaNutricionalFactory(List<EstrategiaMetaNutricional> estrategias) {
        this.estrategiasPorTipo = estrategias.stream()
                .flatMap(estrategia ->
                        estrategia.tiposSuportados().stream().map(tipo -> Map.entry(tipo, estrategia)))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public EstrategiaMetaNutricional obter(TipoObjetivo tipoObjetivo) {
        EstrategiaMetaNutricional estrategia = estrategiasPorTipo.get(tipoObjetivo);
        if (estrategia == null) {
            throw new BusinessException("Não há estratégia de cálculo nutricional para o objetivo " + tipoObjetivo);
        }
        return estrategia;
    }
}
