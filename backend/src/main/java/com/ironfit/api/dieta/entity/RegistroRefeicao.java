package com.ironfit.api.dieta.entity;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Registro do diário alimentar: marca que o usuário consumiu uma refeição planejada em um dia específico. */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registros_refeicao")
public class RegistroRefeicao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refeicao_id", nullable = false)
    private Refeicao refeicao;

    @Column(name = "data_consumo", nullable = false)
    private LocalDate dataConsumo;

    @Column(name = "consumido_em", nullable = false)
    @Builder.Default
    private Instant consumidoEm = Instant.now();
}
