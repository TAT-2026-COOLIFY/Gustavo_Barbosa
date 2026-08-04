package com.ironfit.api.dieta.entity;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.common.BaseEntity;
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
@Table(name = "alimentos")
public class Alimento extends BaseEntity {

    /** NULL indica um alimento do catálogo do sistema, visível a todos os usuários. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 100)
    private String marca;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaAlimento categoria;

    @Column(name = "calorias_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal caloriasPor100g;

    @Column(name = "proteina_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal proteinaPor100g;

    @Column(name = "carboidrato_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal carboidratoPor100g;

    @Column(name = "gordura_100g", nullable = false, precision = 6, scale = 2)
    private BigDecimal gorduraPor100g;

    @Column(name = "fibra_100g", precision = 6, scale = 2)
    private BigDecimal fibraPor100g;
}
