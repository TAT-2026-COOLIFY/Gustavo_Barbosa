package com.ironfit.api.treino.repository;

import com.ironfit.api.treino.entity.Exercicio;
import com.ironfit.api.treino.entity.GrupoMuscular;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    List<Exercicio> findByGrupoMuscularOrderByNome(GrupoMuscular grupoMuscular);

    List<Exercicio> findAllByOrderByNome();
}
