package com.ironfit.api.ia.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Uma sugestão de treino ou dieta gerada pelo assistente de IA, pendente de revisão do usuário.
 * {@code conteudoJson} guarda o payload no mesmo formato de {@code TreinoRequestDTO}/{@code DietaRequestDTO},
 * já com ids reais do catálogo (exercícios/alimentos) — confirmar o rascunho apenas encaminha esse
 * payload (possivelmente editado pelo usuário) para o service de Treino/Dieta já existente, com origem IA.
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rascunhos_ia")
public class RascunhoIa extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversa_id")
    private ConversaIa conversa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensagem_id")
    private MensagemIa mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoRascunhoIa tipo;

    @Column(name = "conteudo_json", nullable = false, columnDefinition = "TEXT")
    private String conteudoJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusRascunhoIa status = StatusRascunhoIa.PENDENTE;
}
