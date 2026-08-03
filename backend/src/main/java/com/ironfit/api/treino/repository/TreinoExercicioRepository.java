package com.ironfit.api.treino.repository;

import com.ironfit.api.treino.entity.TreinoExercicio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreinoExercicioRepository extends JpaRepository<TreinoExercicio, Long> {

    List<TreinoExercicio> findByTreinoIdOrderByOrdem(Long treinoId);

    void deleteByTreinoId(Long treinoId);
}
