package com.ironfit.api.ia.repository;

import com.ironfit.api.ia.entity.MensagemIa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensagemIaRepository extends JpaRepository<MensagemIa, Long> {

    List<MensagemIa> findByConversaIdOrderByCriadoEmAsc(Long conversaId);
}
