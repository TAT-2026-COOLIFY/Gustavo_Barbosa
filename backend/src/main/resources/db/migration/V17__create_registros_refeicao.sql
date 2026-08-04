CREATE TABLE registros_refeicao (
    id             BIGSERIAL PRIMARY KEY,
    usuario_id     BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    refeicao_id    BIGINT      NOT NULL REFERENCES refeicoes (id) ON DELETE CASCADE,
    data_consumo   DATE        NOT NULL,
    consumido_em   TIMESTAMPTZ NOT NULL DEFAULT now(),
    criado_em      TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_registros_refeicao ON registros_refeicao (usuario_id, refeicao_id, data_consumo);
CREATE INDEX idx_registros_refeicao_usuario_data ON registros_refeicao (usuario_id, data_consumo);
