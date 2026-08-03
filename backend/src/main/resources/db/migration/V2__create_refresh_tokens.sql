CREATE TABLE refresh_tokens (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT       NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    token_hash    VARCHAR(255) NOT NULL,
    expira_em     TIMESTAMPTZ  NOT NULL,
    revogado      BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_usuario_id ON refresh_tokens (usuario_id);
CREATE UNIQUE INDEX uq_refresh_tokens_token_hash ON refresh_tokens (token_hash);
