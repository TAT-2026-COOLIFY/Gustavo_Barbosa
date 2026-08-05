CREATE TABLE registros_progresso (
    id                        BIGSERIAL PRIMARY KEY,
    usuario_id                BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    data_registro             DATE        NOT NULL,
    peso_kg                   NUMERIC(5, 1) NOT NULL,
    percentual_gordura        NUMERIC(4, 1),
    circunferencia_cintura_cm NUMERIC(5, 1),
    circunferencia_quadril_cm NUMERIC(5, 1),
    circunferencia_braco_cm   NUMERIC(5, 1),
    circunferencia_coxa_cm    NUMERIC(5, 1),
    circunferencia_peito_cm   NUMERIC(5, 1),
    foto_url                  VARCHAR(500),
    observacoes               VARCHAR(500),
    criado_em                 TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em             TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_registros_progresso_usuario_id ON registros_progresso (usuario_id);
CREATE UNIQUE INDEX uq_registros_progresso_usuario_data ON registros_progresso (usuario_id, data_registro);
