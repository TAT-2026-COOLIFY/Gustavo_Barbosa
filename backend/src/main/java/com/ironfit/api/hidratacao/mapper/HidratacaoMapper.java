package com.ironfit.api.hidratacao.mapper;

import com.ironfit.api.hidratacao.dto.MetaHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.dto.RegistroHidratacaoResponseDTO;
import com.ironfit.api.hidratacao.entity.MetaHidratacao;
import com.ironfit.api.hidratacao.entity.RegistroHidratacao;
import org.springframework.stereotype.Component;

@Component
public class HidratacaoMapper {

    public MetaHidratacaoResponseDTO toResponseDTO(MetaHidratacao meta) {
        return new MetaHidratacaoResponseDTO(meta.getId(), meta.getMetaMl());
    }

    public RegistroHidratacaoResponseDTO toResponseDTO(RegistroHidratacao registro) {
        return new RegistroHidratacaoResponseDTO(registro.getId(), registro.getQuantidadeMl(), registro.getRegistradoEm());
    }
}
