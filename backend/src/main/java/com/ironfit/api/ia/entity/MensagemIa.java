package com.ironfit.api.ia.entity;

import com.ironfit.api.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Uma mensagem (do usuário ou do assistente) dentro de uma {@link ConversaIa}. */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mensagens_ia")
public class MensagemIa extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversa_id", nullable = false)
    private ConversaIa conversa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PapelMensagemIa papel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;
}
