package com.ironfit.api.meditacao.entity;

import com.ironfit.api.auth.entity.Usuario;
import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Uma execução (em andamento ou concluída) de uma {@link Meditacao} por um usuário. */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sessoes_meditacao")
public class SessaoMeditacao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meditacao_id", nullable = false)
    private Meditacao meditacao;

    @Column(name = "data_realizacao", nullable = false)
    private Instant dataRealizacao;

    @Column(name = "duracao_segundos")
    private Integer duracaoSegundos;

    @Column(nullable = false)
    @Builder.Default
    private boolean concluido = false;
}
