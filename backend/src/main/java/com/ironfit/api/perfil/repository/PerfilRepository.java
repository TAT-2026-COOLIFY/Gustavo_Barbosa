package com.ironfit.api.perfil.repository;

import com.ironfit.api.perfil.entity.Perfil;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByUsuarioId(Long usuarioId);
}
