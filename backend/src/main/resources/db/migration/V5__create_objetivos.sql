CREATE TABLE objetivos (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tipo           VARCHAR(30) NOT NULL,
    peso_meta_kg   NUMERIC(5, 1),
    data_inicio    DATE        NOT NULL,
    data_alvo      DATE,
    ativo          BOOLEAN     NOT NULL DEFAULT TRUE,
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_objetivos_usuario_id ON objetivos (usuario_id);
CREATE UNIQUE INDEX uq_objetivos_usuario_ativo ON objetivos (usuario_id) WHERE ativo;
