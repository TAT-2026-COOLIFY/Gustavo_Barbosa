package com.ironfit.api.dieta.repository;

import com.ironfit.api.dieta.entity.RegistroRefeicao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroRefeicaoRepository extends JpaRepository<RegistroRefeicao, Long> {

    List<RegistroRefeicao> findByUsuarioIdAndDataConsumo(Long usuarioId, LocalDate dataConsumo);

    Optional<RegistroRefeicao> findByUsuarioIdAndRefeicaoIdAndDataConsumo(
            Long usuarioId, Long refeicaoId, LocalDate dataConsumo);

    void deleteByUsuarioIdAndRefeicaoIdAndDataConsumo(Long usuarioId, Long refeicaoId, LocalDate dataConsumo);

    List<RegistroRefeicao> findByUsuarioIdAndDataConsumoBetween(Long usuarioId, LocalDate inicio, LocalDate fim);
}
