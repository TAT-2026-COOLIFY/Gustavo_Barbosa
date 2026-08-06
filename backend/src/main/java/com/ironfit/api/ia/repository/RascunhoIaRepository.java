package com.ironfit.api.ia.repository;

import com.ironfit.api.ia.entity.RascunhoIa;
import com.ironfit.api.ia.entity.StatusRascunhoIa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RascunhoIaRepository extends JpaRepository<RascunhoIa, Long> {

    Optional<RascunhoIa> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<RascunhoIa> findByMensagemIdIn(List<Long> mensagemIds);

    boolean existsByUsuarioIdAndStatus(Long usuarioId, StatusRascunhoIa status);
}
