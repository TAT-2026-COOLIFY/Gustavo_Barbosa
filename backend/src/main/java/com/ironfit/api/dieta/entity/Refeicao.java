package com.ironfit.api.dieta.entity;

import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
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
@Table(name = "refeicoes")
public class Refeicao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dieta_id", nullable = false)
    private Dieta dieta;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private Integer ordem;

    @Column(name = "horario_sugerido")
    private LocalTime horarioSugerido;
}
