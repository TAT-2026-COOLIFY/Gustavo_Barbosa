package com.ironfit.api.dieta.repository;

import com.ironfit.api.dieta.entity.Dieta;
import com.ironfit.api.dieta.entity.OrigemDieta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DietaRepository extends JpaRepository<Dieta, Long> {

    List<Dieta> findByUsuarioIdOrderByAtualizadoEmDesc(Long usuarioId);

    List<Dieta> findByUsuarioIsNullAndOrigemOrderByNome(OrigemDieta origem);

    Optional<Dieta> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Dieta> findByUsuarioIdAndAtivaTrue(Long usuarioId);

    Optional<Dieta> findFirstByUsuarioIdAndTemplateOrigemIdOrderByCriadoEmDesc(Long usuarioId, Long templateOrigemId);

    @Modifying
    @Query("UPDATE Dieta d SET d.ativa = false WHERE d.usuario.id = :usuarioId AND d.ativa = true")
    void desativarTodasDoUsuario(@Param("usuarioId") Long usuarioId);
}
