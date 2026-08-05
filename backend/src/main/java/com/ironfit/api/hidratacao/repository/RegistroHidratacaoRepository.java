package com.ironfit.api.hidratacao.repository;

import com.ironfit.api.hidratacao.entity.RegistroHidratacao;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroHidratacaoRepository extends JpaRepository<RegistroHidratacao, Long> {

    List<RegistroHidratacao> findByUsuarioIdAndDataRegistroOrderByRegistradoEmDesc(Long usuarioId, LocalDate dataRegistro);

    List<RegistroHidratacao> findByUsuarioIdAndDataRegistroBetween(Long usuarioId, LocalDate inicio, LocalDate fim);

    Optional<RegistroHidratacao> findByIdAndUsuarioId(Long id, Long usuarioId);
}
