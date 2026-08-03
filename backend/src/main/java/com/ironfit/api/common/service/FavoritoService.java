package com.ironfit.api.common.service;

import com.ironfit.api.auth.repository.UsuarioRepository;
import com.ironfit.api.common.Favorito;
import com.ironfit.api.common.FavoritoTipo;
import com.ironfit.api.common.repository.FavoritoRepository;
import com.ironfit.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;

    public FavoritoService(FavoritoRepository favoritoRepository, UsuarioRepository usuarioRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public boolean isFavorito(Long usuarioId, FavoritoTipo tipoItem, Long itemId) {
        return favoritoRepository.findByUsuarioIdAndTipoItemAndItemId(usuarioId, tipoItem, itemId).isPresent();
    }

    @Transactional
    public boolean alternar(Long usuarioId, FavoritoTipo tipoItem, Long itemId) {
        var existente = favoritoRepository.findByUsuarioIdAndTipoItemAndItemId(usuarioId, tipoItem, itemId);

        if (existente.isPresent()) {
            favoritoRepository.delete(existente.get());
            return false;
        }

        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        favoritoRepository.save(
                Favorito.builder().usuario(usuario).tipoItem(tipoItem).itemId(itemId).build());
        return true;
    }
}
