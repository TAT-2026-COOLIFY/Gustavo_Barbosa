package com.ironfit.api.dieta.repository;

import com.ironfit.api.dieta.entity.Alimento;
import com.ironfit.api.dieta.entity.CategoriaAlimento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlimentoRepository extends JpaRepository<Alimento, Long> {

    @Query("""
            SELECT a FROM Alimento a
            WHERE (a.usuario IS NULL OR a.usuario.id = :usuarioId)
              AND (:q IS NULL OR LOWER(a.nome) LIKE LOWER(CONCAT('%', CAST(:q AS string), '%')))
              AND (:categoria IS NULL OR a.categoria = :categoria)
            ORDER BY a.nome
            """)
    List<Alimento> buscar(
            @Param("usuarioId") Long usuarioId, @Param("q") String q, @Param("categoria") CategoriaAlimento categoria);

    Optional<Alimento> findByIdAndUsuarioId(Long id, Long usuarioId);
}
