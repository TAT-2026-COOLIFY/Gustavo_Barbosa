CREATE TABLE treinos (
    id                 BIGSERIAL PRIMARY KEY,
    usuario_id         BIGINT      REFERENCES usuarios (id) ON DELETE CASCADE,
    nome               VARCHAR(120) NOT NULL,
    origem             VARCHAR(20)  NOT NULL,
    template_origem_id BIGINT      REFERENCES treinos (id) ON DELETE SET NULL,
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_treinos_usuario_id ON treinos (usuario_id);
