package com.ironfit.api.common.repository;

import com.ironfit.api.common.Favorito;
import com.ironfit.api.common.FavoritoTipo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    Optional<Favorito> findByUsuarioIdAndTipoItemAndItemId(Long usuarioId, FavoritoTipo tipoItem, Long itemId);
}
