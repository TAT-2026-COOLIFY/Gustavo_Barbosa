package com.ironfit.api.hidratacao.repository;

import com.ironfit.api.hidratacao.entity.MetaHidratacao;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MetaHidratacaoRepository extends JpaRepository<MetaHidratacao, Long> {

    Optional<MetaHidratacao> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    @Modifying
    @Query("UPDATE MetaHidratacao m SET m.ativo = false WHERE m.usuario.id = :usuarioId AND m.ativo = true")
    void desativarTodasDoUsuario(@Param("usuarioId") Long usuarioId);
}
