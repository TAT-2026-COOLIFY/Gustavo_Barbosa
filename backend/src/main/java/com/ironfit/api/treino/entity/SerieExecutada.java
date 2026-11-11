package com.ironfit.api.treino.entity;

import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
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
@Table(name = "series_executadas")
public class SerieExecutada extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_treino_id", nullable = false)
    private SessaoTreino sessaoTreino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_exercicio_id")
    private TreinoExercicio treinoExercicio;

    @Column(name = "numero_serie", nullable = false)
    private Integer numeroSerie;

    @Column(name = "repeticoes_realizadas", nullable = false)
    private Integer repeticoesRealizadas;

    @Column(name = "carga_realizada_kg")
    private BigDecimal cargaRealizadaKg;
}
