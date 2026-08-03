CREATE TABLE sessoes_treino (
    id                BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    treino_id         BIGINT      NOT NULL REFERENCES treinos (id),
    data_execucao     TIMESTAMPTZ NOT NULL DEFAULT now(),
    duracao_segundos  INTEGER,
    concluido         BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em         TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sessoes_treino_usuario_id ON sessoes_treino (usuario_id, data_execucao);
