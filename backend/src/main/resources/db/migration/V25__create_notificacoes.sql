CREATE TABLE notificacoes (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT       NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tipo           VARCHAR(30)  NOT NULL,
    titulo         VARCHAR(120) NOT NULL,
    mensagem       VARCHAR(500) NOT NULL,
    lida           BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_notificacoes_usuario_id ON notificacoes (usuario_id);
