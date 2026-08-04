CREATE TABLE refeicoes_alimentos (
    id                 BIGSERIAL PRIMARY KEY,
    refeicao_id        BIGINT       NOT NULL REFERENCES refeicoes (id) ON DELETE CASCADE,
    alimento_id        BIGINT       NOT NULL REFERENCES alimentos (id) ON DELETE RESTRICT,
    quantidade_gramas  NUMERIC(6, 1) NOT NULL,
    ordem              INTEGER      NOT NULL,
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refeicoes_alimentos_refeicao_id ON refeicoes_alimentos (refeicao_id);
CREATE INDEX idx_refeicoes_alimentos_alimento_id ON refeicoes_alimentos (alimento_id);
