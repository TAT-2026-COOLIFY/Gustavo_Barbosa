CREATE TABLE treino_exercicios (
    id                 BIGSERIAL PRIMARY KEY,
    treino_id          BIGINT      NOT NULL REFERENCES treinos (id) ON DELETE CASCADE,
    exercicio_id       BIGINT      NOT NULL REFERENCES exercicios (id),
    ordem              INTEGER     NOT NULL,
    series_alvo        INTEGER     NOT NULL,
    repeticoes_alvo    INTEGER     NOT NULL,
    carga_alvo_kg      NUMERIC(6, 2),
    descanso_segundos  INTEGER,
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_treino_exercicios_treino_id ON treino_exercicios (treino_id);
