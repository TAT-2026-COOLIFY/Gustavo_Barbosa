package com.ironfit.api.meditacao.repository;

import com.ironfit.api.meditacao.entity.SessaoMeditacao;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoMeditacaoRepository extends JpaRepository<SessaoMeditacao, Long> {

    Optional<SessaoMeditacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<SessaoMeditacao> findByUsuarioIdAndConcluidoTrueOrderByDataRealizacaoDesc(Long usuarioId);

    List<SessaoMeditacao> findByUsuarioIdAndConcluidoTrueAndDataRealizacaoAfterOrderByDataRealizacaoDesc(
            Long usuarioId, Instant desde);
}
