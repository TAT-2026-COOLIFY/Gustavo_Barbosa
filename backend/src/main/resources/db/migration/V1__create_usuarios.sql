CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(120)  NOT NULL,
    email           VARCHAR(180)  NOT NULL,
    senha_hash      VARCHAR(255)  NOT NULL,
    foto_perfil_url VARCHAR(500),
    ativo           BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMPTZ   NOT NULL DEFAULT now(),
    atualizado_em   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_usuarios_email ON usuarios (LOWER(email));
