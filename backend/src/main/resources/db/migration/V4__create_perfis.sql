CREATE TABLE perfis (
    id                 BIGSERIAL PRIMARY KEY,
    usuario_id         BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    data_nascimento    DATE,
    sexo               VARCHAR(20),
    altura_cm          NUMERIC(5, 1),
    nivel_experiencia  VARCHAR(20),
    bio                VARCHAR(500),
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_perfis_usuario_id ON perfis (usuario_id);
