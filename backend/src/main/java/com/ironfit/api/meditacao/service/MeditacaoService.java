package com.ironfit.api.meditacao.service;

import com.ironfit.api.exception.ResourceNotFoundException;
import com.ironfit.api.meditacao.dto.MeditacaoResponseDTO;
import com.ironfit.api.meditacao.entity.Meditacao;
import com.ironfit.api.meditacao.entity.TipoMeditacao;
import com.ironfit.api.meditacao.mapper.MeditacaoMapper;
import com.ironfit.api.meditacao.repository.MeditacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/** Catálogo (somente leitura) de sessões guiadas de meditação. */
@Service
public class MeditacaoService {

    private final MeditacaoRepository meditacaoRepository;
    private final MeditacaoMapper meditacaoMapper;

    public MeditacaoService(MeditacaoRepository meditacaoRepository, MeditacaoMapper meditacaoMapper) {
        this.meditacaoRepository = meditacaoRepository;
        this.meditacaoMapper = meditacaoMapper;
    }

    public List<MeditacaoResponseDTO> listar(TipoMeditacao tipo) {
        List<Meditacao> meditacoes =
                tipo != null ? meditacaoRepository.findByTipoOrderByNome(tipo) : meditacaoRepository.findAllByOrderByNome();
        return meditacoes.stream().map(meditacaoMapper::toResponseDTO).toList();
    }

    public MeditacaoResponseDTO obter(Long id) {
        return meditacaoMapper.toResponseDTO(buscar(id));
    }

    public Meditacao buscar(Long id) {
        return meditacaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Meditação não encontrada"));
    }
}
