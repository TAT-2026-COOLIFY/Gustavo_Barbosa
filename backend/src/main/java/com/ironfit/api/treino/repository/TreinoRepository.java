package com.ironfit.api.treino.repository;

import com.ironfit.api.treino.entity.OrigemTreino;
import com.ironfit.api.treino.entity.Treino;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    List<Treino> findByUsuarioIdOrderByAtualizadoEmDesc(Long usuarioId);

    List<Treino> findByUsuarioIsNullAndOrigemOrderByNome(OrigemTreino origem);

    Optional<Treino> findByIdAndUsuarioId(Long id, Long usuarioId);
}
