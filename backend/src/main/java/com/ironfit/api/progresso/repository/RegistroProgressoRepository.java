package com.ironfit.api.progresso.repository;

import com.ironfit.api.progresso.entity.RegistroProgresso;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroProgressoRepository extends JpaRepository<RegistroProgresso, Long> {

    List<RegistroProgresso> findByUsuarioIdOrderByDataRegistroDesc(Long usuarioId);

    Optional<RegistroProgresso> findFirstByUsuarioIdOrderByDataRegistroDesc(Long usuarioId);

    Optional<RegistroProgresso> findByUsuarioIdAndDataRegistro(Long usuarioId, LocalDate dataRegistro);

    Optional<RegistroProgresso> findByIdAndUsuarioId(Long id, Long usuarioId);
}
