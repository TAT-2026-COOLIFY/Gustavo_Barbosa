CREATE TABLE favoritos (
    id           BIGSERIAL PRIMARY KEY,
    usuario_id   BIGINT      NOT NULL REFERENCES usuarios (id) ON DELETE CASCADE,
    tipo_item    VARCHAR(20) NOT NULL,
    item_id      BIGINT      NOT NULL,
    criado_em    TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_favoritos_usuario_tipo_item ON favoritos (usuario_id, tipo_item, item_id);
