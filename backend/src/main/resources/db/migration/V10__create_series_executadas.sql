CREATE TABLE series_executadas (
    id                       BIGSERIAL PRIMARY KEY,
    sessao_treino_id         BIGINT      NOT NULL REFERENCES sessoes_treino (id) ON DELETE CASCADE,
    treino_exercicio_id      BIGINT      NOT NULL REFERENCES treino_exercicios (id),
    numero_serie             INTEGER     NOT NULL,
    repeticoes_realizadas    INTEGER     NOT NULL,
    carga_realizada_kg       NUMERIC(6, 2),
    criado_em                TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_series_executadas_sessao_treino_id ON series_executadas (sessao_treino_id);
