CREATE TABLE conversas_ia (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    titulo         VARCHAR(120),
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_conversas_ia_usuario_id ON conversas_ia (usuario_id);

CREATE TABLE mensagens_ia (
    id             BIGSERIAL PRIMARY KEY,
    conversa_id    BIGINT      NOT NULL REFERENCES conversas_ia (id) ON DELETE CASCADE,
    papel          VARCHAR(20) NOT NULL,
    conteudo       TEXT        NOT NULL,
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_mensagens_ia_conversa_id ON mensagens_ia (conversa_id);

CREATE TABLE rascunhos_ia (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    conversa_id     BIGINT      REFERENCES conversas_ia (id) ON DELETE SET NULL,
    mensagem_id     BIGINT      REFERENCES mensagens_ia (id) ON DELETE SET NULL,
    tipo            VARCHAR(20) NOT NULL,
    conteudo_json   TEXT        NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_rascunhos_ia_usuario_id ON rascunhos_ia (usuario_id);
