package com.ironfit.api.perfil.repository;

import com.ironfit.api.perfil.entity.Objetivo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObjetivoRepository extends JpaRepository<Objetivo, Long> {

    Optional<Objetivo> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<Objetivo> findByUsuarioIdOrderByDataInicioDesc(Long usuarioId);

    @Modifying
    @Query("UPDATE Objetivo o SET o.ativo = false WHERE o.usuario.id = :usuarioId AND o.ativo = true")
    void desativarTodosDoUsuario(@Param("usuarioId") Long usuarioId);
}
