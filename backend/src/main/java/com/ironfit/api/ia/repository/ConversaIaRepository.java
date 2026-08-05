package com.ironfit.api.ia.repository;

import com.ironfit.api.ia.entity.ConversaIa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversaIaRepository extends JpaRepository<ConversaIa, Long> {

    List<ConversaIa> findByUsuarioIdOrderByAtualizadoEmDesc(Long usuarioId);

    Optional<ConversaIa> findByIdAndUsuarioId(Long id, Long usuarioId);
}
