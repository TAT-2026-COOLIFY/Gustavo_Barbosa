CREATE TABLE refeicoes (
    id                BIGSERIAL PRIMARY KEY,
    dieta_id          BIGINT      NOT NULL REFERENCES dietas (id) ON DELETE CASCADE,
    nome              VARCHAR(80) NOT NULL,
    ordem             INTEGER     NOT NULL,
    horario_sugerido  TIME,
    criado_em         TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refeicoes_dieta_id ON refeicoes (dieta_id);
