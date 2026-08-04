CREATE TABLE dietas (
    id                          BIGSERIAL PRIMARY KEY,
    usuario_id                  BIGINT      REFERENCES usuarios (id) ON DELETE CASCADE,
    nome                        VARCHAR(120) NOT NULL,
    origem                      VARCHAR(20)  NOT NULL,
    template_origem_id          BIGINT      REFERENCES dietas (id) ON DELETE SET NULL,
    ativa                       BOOLEAN     NOT NULL DEFAULT FALSE,
    peso_referencia_kg          NUMERIC(5, 1),
    nivel_atividade             VARCHAR(20),
    tipo_objetivo_referencia    VARCHAR(30),
    calculo_automatico          BOOLEAN     NOT NULL DEFAULT TRUE,
    meta_calorias_kcal          INTEGER     NOT NULL,
    meta_proteina_g             INTEGER     NOT NULL,
    meta_carboidrato_g          INTEGER     NOT NULL,
    meta_gordura_g              INTEGER     NOT NULL,
    meta_agua_ml                INTEGER     NOT NULL,
    criado_em                   TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizado_em                TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_dietas_usuario_id ON dietas (usuario_id);
CREATE UNIQUE INDEX uq_dietas_usuario_ativa ON dietas (usuario_id) WHERE ativa;
