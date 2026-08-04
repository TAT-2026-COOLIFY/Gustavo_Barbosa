package com.ironfit.api.dieta.dto;

import com.ironfit.api.dieta.entity.NivelAtividade;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * Se {@code calculoAutomatico} for true, {@code pesoReferenciaKg}, {@code nivelAtividade} e {@code tipoObjetivoReferencia}
 * são obrigatórios e as metas são calculadas pelo servidor (ignorando valores manuais enviados).
 * Se for false, os campos de meta (meta*) são obrigatórios e usados como informados.
 * Essa validação cruzada é feita no service, não em anotações de bean validation.
 */
public record DietaRequestDTO(
        @NotBlank
                @Size(max = 120)
                String nome,
        @DecimalMin(value = "20.0", message = "Peso de referência deve ser ao menos 20kg") BigDecimal pesoReferenciaKg,
        NivelAtividade nivelAtividade,
        TipoObjetivo tipoObjetivoReferencia,
        boolean calculoAutomatico,
        Integer metaCaloriasKcal,
        Integer metaProteinaG,
        Integer metaCarboidratoG,
        Integer metaGorduraG,
        Integer metaAguaMl,
        @NotEmpty(message = "Dieta deve ter ao menos uma refeição") @Valid List<RefeicaoRequestDTO> refeicoes) {}
