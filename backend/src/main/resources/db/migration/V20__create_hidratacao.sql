CREATE TABLE metas_hidratacao (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    meta_ml        INTEGER     NOT NULL,
    ativo          BOOLEAN     NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_metas_hidratacao_usuario_id ON metas_hidratacao (usuario_id);
CREATE UNIQUE INDEX uq_metas_hidratacao_usuario_ativo ON metas_hidratacao (usuario_id) WHERE ativo;

CREATE TABLE registros_hidratacao (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    quantidade_ml  INTEGER     NOT NULL,
    data_registro  DATE        NOT NULL,
    registrado_em  TIMESTAMPTZ NOT NULL DEFAULT now(),
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_registros_hidratacao_usuario_data ON registros_hidratacao (usuario_id, data_registro);
