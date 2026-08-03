CREATE TABLE password_reset_tokens (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT       NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    token_hash    VARCHAR(255) NOT NULL,
    expira_em     TIMESTAMPTZ  NOT NULL,
    usado         BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_password_reset_tokens_usuario_id ON password_reset_tokens (usuario_id);
CREATE UNIQUE INDEX uq_password_reset_tokens_token_hash ON password_reset_tokens (token_hash);
