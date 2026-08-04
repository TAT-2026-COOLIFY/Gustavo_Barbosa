package com.ironfit.api.dieta.entity;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.common.BaseEntity;
import com.ironfit.api.perfil.entity.TipoObjetivo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dietas")
public class Dieta extends BaseEntity {

    /** NULL indica uma dieta-modelo do sistema, visível a todos os usuários. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemDieta origem;

    @Column(name = "template_origem_id")
    private Long templateOrigemId;

    /** Indica se esta é a dieta que o usuário está seguindo no momento (uma por vez). */
    @Column(nullable = false)
    @Builder.Default
    private boolean ativa = false;

    /** Peso informado no momento da criação/cálculo, usado apenas como referência para a fórmula de TMB. */
    @Column(name = "peso_referencia_kg", precision = 5, scale = 1)
    private BigDecimal pesoReferenciaKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_atividade", length = 20)
    private NivelAtividade nivelAtividade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_objetivo_referencia", length = 30)
    private TipoObjetivo tipoObjetivoReferencia;

    /** Se true, as metas foram calculadas automaticamente (TMB + estratégia do objetivo); se false, foram informadas manualmente. */
    @Column(name = "calculo_automatico", nullable = false)
    @Builder.Default
    private boolean calculoAutomatico = true;

    @Column(name = "meta_calorias_kcal", nullable = false)
    private Integer metaCaloriasKcal;

    @Column(name = "meta_proteina_g", nullable = false)
    private Integer metaProteinaG;

    @Column(name = "meta_carboidrato_g", nullable = false)
    private Integer metaCarboidratoG;

    @Column(name = "meta_gordura_g", nullable = false)
    private Integer metaGorduraG;

    @Column(name = "meta_agua_ml", nullable = false)
    private Integer metaAguaMl;
}
