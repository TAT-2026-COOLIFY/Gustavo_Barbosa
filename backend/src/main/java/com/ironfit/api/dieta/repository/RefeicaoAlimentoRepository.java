package com.ironfit.api.dieta.repository;

import com.ironfit.api.dieta.entity.RefeicaoAlimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefeicaoAlimentoRepository extends JpaRepository<RefeicaoAlimento, Long> {

    List<RefeicaoAlimento> findByRefeicaoIdOrderByOrdem(Long refeicaoId);

    boolean existsByAlimentoId(Long alimentoId);
}
