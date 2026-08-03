package com.ironfit.api.auth.repository;

import com.ironfit.api.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHashAndRevogadoFalse(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revogado = true WHERE r.usuario.id = :usuarioId AND r.revogado = false")
    void revogarTodosDoUsuario(@Param("usuarioId") Long usuarioId);
}
