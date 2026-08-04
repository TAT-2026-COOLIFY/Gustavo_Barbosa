CREATE TABLE alimentos (
    id                BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT       REFERENCES usuarios (id) ON DELETE CASCADE,
    nome              VARCHAR(150) NOT NULL,
    marca             VARCHAR(100),
    categoria         VARCHAR(20)  NOT NULL,
    calorias_100g     NUMERIC(6, 2) NOT NULL,
    proteina_100g     NUMERIC(6, 2) NOT NULL,
    carboidrato_100g  NUMERIC(6, 2) NOT NULL,
    gordura_100g      NUMERIC(6, 2) NOT NULL,
    fibra_100g        NUMERIC(6, 2),
    criado_em         TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_alimentos_usuario_id ON alimentos (usuario_id);
CREATE INDEX idx_alimentos_nome ON alimentos (LOWER(nome));
