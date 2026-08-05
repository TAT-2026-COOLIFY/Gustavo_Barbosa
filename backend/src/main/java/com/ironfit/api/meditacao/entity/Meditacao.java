package com.ironfit.api.meditacao.entity;

import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Sessão guiada de meditação do catálogo do sistema. Diferente de Exercicio/Alimento, não existe
 * versão privada por usuário aqui — o catálogo é inteiramente curado pelo sistema (sem necessidade
 * real de personalização identificada no escopo deste módulo).
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meditacoes")
public class Meditacao extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMeditacao tipo;

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @Column(length = 500)
    private String descricao;

    @Column(columnDefinition = "TEXT")
    private String instrucoes;

    /** Padrão de respiração guiada (em segundos); null para sessões que não são de respiração. */
    @Column(name = "inspirar_segundos")
    private Integer inspirarSegundos;

    @Column(name = "segurar_segundos")
    private Integer segurarSegundos;

    @Column(name = "expirar_segundos")
    private Integer expirarSegundos;
}
