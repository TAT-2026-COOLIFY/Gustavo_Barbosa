package com.ironfit.api.treino.repository;

import com.ironfit.api.treino.entity.SessaoTreino;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoTreinoRepository extends JpaRepository<SessaoTreino, Long> {

    List<SessaoTreino> findByUsuarioIdOrderByDataExecucaoDesc(Long usuarioId);

    Optional<SessaoTreino> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndConcluidoTrueAndDataExecucaoBetween(Long usuarioId, Instant inicio, Instant fim);
}
