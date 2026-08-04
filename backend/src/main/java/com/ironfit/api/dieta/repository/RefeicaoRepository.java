package com.ironfit.api.dieta.repository;

import com.ironfit.api.dieta.entity.Refeicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefeicaoRepository extends JpaRepository<Refeicao, Long> {

    List<Refeicao> findByDietaIdOrderByOrdem(Long dietaId);

    void deleteByDietaId(Long dietaId);
}
