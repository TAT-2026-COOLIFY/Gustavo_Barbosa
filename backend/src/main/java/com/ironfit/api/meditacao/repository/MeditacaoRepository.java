package com.ironfit.api.meditacao.repository;

import com.ironfit.api.meditacao.entity.Meditacao;
import com.ironfit.api.meditacao.entity.TipoMeditacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeditacaoRepository extends JpaRepository<Meditacao, Long> {

    List<Meditacao> findAllByOrderByNome();

    List<Meditacao> findByTipoOrderByNome(TipoMeditacao tipo);
}
