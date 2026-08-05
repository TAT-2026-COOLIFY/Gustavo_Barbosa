package com.ironfit.api.progresso.entity;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Um "check-in" de progresso físico do usuário em uma data (peso, % de gordura, circunferências,
 * foto de evolução). Único por (usuario, dataRegistro) — registrar de novo no mesmo dia atualiza o existente.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registros_progresso")
public class RegistroProgresso extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_registro", nullable = false)
    private LocalDate dataRegistro;

    @Column(name = "peso_kg", nullable = false)
    private BigDecimal pesoKg;

    @Column(name = "percentual_gordura")
    private BigDecimal percentualGordura;

    @Column(name = "circunferencia_cintura_cm")
    private BigDecimal circunferenciaCinturaCm;

    @Column(name = "circunferencia_quadril_cm")
    private BigDecimal circunferenciaQuadrilCm;

    @Column(name = "circunferencia_braco_cm")
    private BigDecimal circunferenciaBracoCm;

    @Column(name = "circunferencia_coxa_cm")
    private BigDecimal circunferenciaCoxaCm;

    @Column(name = "circunferencia_peito_cm")
    private BigDecimal circunferenciaPeitoCm;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(length = 500)
    private String observacoes;
}
