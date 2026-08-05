CREATE TABLE meditacoes (
    id                 BIGSERIAL PRIMARY KEY,
    nome               VARCHAR(120) NOT NULL,
    tipo               VARCHAR(30)  NOT NULL,
    duracao_minutos    INTEGER      NOT NULL,
    descricao          VARCHAR(500),
    instrucoes         TEXT,
    inspirar_segundos  INTEGER,
    segurar_segundos   INTEGER,
    expirar_segundos   INTEGER,
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE sessoes_meditacao (
    id                BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    meditacao_id      BIGINT      NOT NULL REFERENCES meditacoes (id),
    data_realizacao   TIMESTAMPTZ NOT NULL,
    duracao_segundos  INTEGER,
    concluido         BOOLEAN     NOT NULL DEFAULT FALSE,
    criado_em         TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sessoes_meditacao_usuario_id ON sessoes_meditacao (usuario_id);
