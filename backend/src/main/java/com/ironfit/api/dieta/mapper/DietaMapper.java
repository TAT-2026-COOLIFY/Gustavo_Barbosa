package com.ironfit.api.dieta.mapper;

import com.ironfit.api.dieta.dto.DietaResponseDTO;
import com.ironfit.api.dieta.dto.DietaResumoDTO;
import com.ironfit.api.dieta.dto.RefeicaoResponseDTO;
import com.ironfit.api.dieta.entity.Dieta;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DietaMapper {

    public DietaResponseDTO toResponseDTO(Dieta dieta, boolean favorito, List<RefeicaoResponseDTO> refeicoes) {
        return new DietaResponseDTO(
                dieta.getId(),
                dieta.getNome(),
                dieta.getOrigem(),
                dieta.isAtiva(),
                favorito,
                dieta.getPesoReferenciaKg(),
                dieta.getNivelAtividade(),
                dieta.getTipoObjetivoReferencia(),
                dieta.isCalculoAutomatico(),
                dieta.getMetaCaloriasKcal(),
                dieta.getMetaProteinaG(),
                dieta.getMetaCarboidratoG(),
                dieta.getMetaGorduraG(),
                dieta.getMetaAguaMl(),
                refeicoes);
    }

    public DietaResumoDTO toResumoDTO(Dieta dieta, boolean favorito, int quantidadeRefeicoes) {
        return new DietaResumoDTO(
                dieta.getId(),
                dieta.getNome(),
                dieta.getOrigem(),
                dieta.isAtiva(),
                favorito,
                dieta.getMetaCaloriasKcal(),
                dieta.getMetaProteinaG(),
                dieta.getMetaCarboidratoG(),
                dieta.getMetaGorduraG(),
                quantidadeRefeicoes);
    }
}
