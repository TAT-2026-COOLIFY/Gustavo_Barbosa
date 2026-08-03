package com.ironfit.api.treino.repository;

import com.ironfit.api.treino.entity.SerieExecutada;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SerieExecutadaRepository extends JpaRepository<SerieExecutada, Long> {

    List<SerieExecutada> findBySessaoTreinoId(Long sessaoTreinoId);

    @Query(
            """
            SELECT COALESCE(SUM(s.cargaRealizadaKg * s.repeticoesRealizadas), 0)
            FROM SerieExecutada s
            WHERE s.sessaoTreino.usuario.id = :usuarioId
              AND s.sessaoTreino.dataExecucao >= :desde
              AND s.cargaRealizadaKg IS NOT NULL
            """)
    BigDecimal calcularVolumeDesde(@Param("usuarioId") Long usuarioId, @Param("desde") Instant desde);
}
