package com.ironfit.api.dieta.service.estrategia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ironfit.api.exception.BusinessException;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import java.util.List;
import org.junit.jupiter.api.Test;

class EstrategiaMetaNutricionalFactoryTest {

    private final EstrategiaMetaNutricionalFactory factory = new EstrategiaMetaNutricionalFactory(
            List.of(new DeficitCaloricoEstrategia(), new SuperavitCaloricoEstrategia(), new ManutencaoEstrategia()));

    @Test
    void deveResolverEstrategiaCorretaParaCadaObjetivo() {
        assertThat(factory.obter(TipoObjetivo.EMAGRECIMENTO)).isInstanceOf(DeficitCaloricoEstrategia.class);
        assertThat(factory.obter(TipoObjetivo.HIPERTROFIA)).isInstanceOf(SuperavitCaloricoEstrategia.class);
        assertThat(factory.obter(TipoObjetivo.MANUTENCAO)).isInstanceOf(ManutencaoEstrategia.class);
        assertThat(factory.obter(TipoObjetivo.CONDICIONAMENTO)).isInstanceOf(ManutencaoEstrategia.class);
        assertThat(factory.obter(TipoObjetivo.SAUDE)).isInstanceOf(ManutencaoEstrategia.class);
    }

    @Test
    void deveLancarBusinessExceptionQuandoNenhumaEstrategiaSuportaOObjetivo() {
        EstrategiaMetaNutricionalFactory factoryVazia = new EstrategiaMetaNutricionalFactory(List.of());

        assertThatThrownBy(() -> factoryVazia.obter(TipoObjetivo.EMAGRECIMENTO)).isInstanceOf(BusinessException.class);
    }
}
