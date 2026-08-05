package com.ironfit.api.meditacao.mapper;

import com.ironfit.api.meditacao.dto.MeditacaoResponseDTO;
import com.ironfit.api.meditacao.dto.SessaoMeditacaoResponseDTO;
import com.ironfit.api.meditacao.entity.Meditacao;
import com.ironfit.api.meditacao.entity.SessaoMeditacao;
import org.springframework.stereotype.Component;

@Component
public class MeditacaoMapper {

    public MeditacaoResponseDTO toResponseDTO(Meditacao meditacao) {
        return new MeditacaoResponseDTO(
                meditacao.getId(),
                meditacao.getNome(),
                meditacao.getTipo(),
                meditacao.getDuracaoMinutos(),
                meditacao.getDescricao(),
                meditacao.getInstrucoes(),
                meditacao.getInspirarSegundos(),
                meditacao.getSegurarSegundos(),
                meditacao.getExpirarSegundos());
    }

    public SessaoMeditacaoResponseDTO toResponseDTO(SessaoMeditacao sessao) {
        Meditacao meditacao = sessao.getMeditacao();
        return new SessaoMeditacaoResponseDTO(
                sessao.getId(),
                meditacao.getId(),
                meditacao.getNome(),
                meditacao.getTipo(),
                meditacao.getDuracaoMinutos(),
                meditacao.getInspirarSegundos(),
                meditacao.getSegurarSegundos(),
                meditacao.getExpirarSegundos(),
                sessao.getDataRealizacao(),
                sessao.getDuracaoSegundos(),
                sessao.isConcluido());
    }
}
