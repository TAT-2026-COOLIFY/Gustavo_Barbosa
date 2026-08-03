CREATE TABLE exercicios (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(120) NOT NULL,
    grupo_muscular VARCHAR(30)  NOT NULL,
    descricao      VARCHAR(500),
    equipamento    VARCHAR(120),
    criado_em      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    atualizado_em  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_exercicios_grupo_muscular ON exercicios (grupo_muscular);

INSERT INTO exercicios (nome, grupo_muscular, descricao, equipamento) VALUES
    ('Supino reto', 'PEITO', 'Empurrar a barra a partir do peito, deitado no banco reto.', 'Barra e banco'),
    ('Supino inclinado', 'PEITO', 'Supino executado em banco inclinado, enfatizando a porção superior do peitoral.', 'Barra e banco inclinado'),
    ('Crucifixo', 'PEITO', 'Abertura de braços com halteres para isolar o peitoral.', 'Halteres'),
    ('Flexão de braço', 'PEITO', 'Flexão de braços no solo usando o peso do próprio corpo.', 'Peso corporal'),
    ('Puxada frontal', 'COSTAS', 'Puxar a barra em pé até a altura do peito, no pulley.', 'Polia alta'),
    ('Remada curvada', 'COSTAS', 'Remada com barra, tronco inclinado à frente.', 'Barra'),
    ('Remada baixa', 'COSTAS', 'Remada sentado, puxando o cabo em direção ao abdômen.', 'Polia baixa'),
    ('Barra fixa', 'COSTAS', 'Puxada do próprio corpo em uma barra fixa.', 'Barra fixa'),
    ('Agachamento livre', 'PERNAS', 'Agachamento com barra livre nas costas.', 'Barra e rack'),
    ('Leg press', 'PERNAS', 'Empurrar a plataforma com as pernas, sentado no aparelho.', 'Leg press'),
    ('Cadeira extensora', 'PERNAS', 'Extensão de joelhos sentado, isolando o quadríceps.', 'Cadeira extensora'),
    ('Cadeira flexora', 'PERNAS', 'Flexão de joelhos, isolando os posteriores de coxa.', 'Cadeira flexora'),
    ('Panturrilha em pé', 'PERNAS', 'Elevação de panturrilha em pé com carga.', 'Máquina de panturrilha'),
    ('Desenvolvimento militar', 'OMBROS', 'Empurrar a barra ou halteres acima da cabeça, em pé ou sentado.', 'Barra ou halteres'),
    ('Elevação lateral', 'OMBROS', 'Elevar halteres lateralmente até a altura dos ombros.', 'Halteres'),
    ('Elevação frontal', 'OMBROS', 'Elevar halteres à frente do corpo até a altura dos ombros.', 'Halteres'),
    ('Rosca direta', 'BRACOS', 'Flexão de cotovelos com barra, isolando o bíceps.', 'Barra'),
    ('Rosca martelo', 'BRACOS', 'Rosca com halteres em pegada neutra.', 'Halteres'),
    ('Tríceps corda', 'BRACOS', 'Extensão de cotovelos na polia alta usando corda.', 'Polia alta e corda'),
    ('Abdominal supra', 'ABDOMEN', 'Flexão do tronco em direção aos joelhos, deitado.', 'Peso corporal'),
    ('Prancha', 'ABDOMEN', 'Sustentação isométrica do corpo alinhado, apoiado nos antebraços.', 'Peso corporal'),
    ('Esteira', 'CARDIO', 'Caminhada ou corrida em esteira.', 'Esteira'),
    ('Bicicleta ergométrica', 'CARDIO', 'Pedalada estacionária de intensidade ajustável.', 'Bicicleta ergométrica');
