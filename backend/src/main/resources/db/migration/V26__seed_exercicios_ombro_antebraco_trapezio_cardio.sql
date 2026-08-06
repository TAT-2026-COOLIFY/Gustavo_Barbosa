-- Novos exercícios para viabilizar treinos-modelo de Ombro, Antebraço, Trapézio e Cardio
-- (Ombro e Cardio já tinham alguns exercícios desde a V6; Antebraço e Trapézio são grupos novos).

INSERT INTO exercicios (nome, grupo_muscular, descricao, equipamento) VALUES
    ('Elevação posterior',       'OMBROS',    'Elevação de halteres com tronco inclinado à frente, isolando o deltoide posterior.', 'Halteres'),
    ('Rosca de punho',           'ANTEBRACO', 'Flexão dos punhos segurando a barra, antebraços apoiados nas coxas ou em um banco.', 'Barra'),
    ('Rosca de punho invertida', 'ANTEBRACO', 'Extensão dos punhos com pegada pronada, isolando a musculatura extensora do antebraço.', 'Barra'),
    ('Farmer''s walk',           'ANTEBRACO', 'Caminhada seguindo halteres ou kettlebells pesados nas mãos, trabalhando pegada e antebraço.', 'Halteres ou kettlebells'),
    ('Encolhimento de ombros',   'TRAPEZIO',  'Elevação vertical dos ombros em direção às orelhas, segurando barra ou halteres.', 'Barra ou halteres'),
    ('Remada alta',              'TRAPEZIO',  'Puxada da barra junto ao corpo até a altura do peito, cotovelos guiando o movimento.', 'Barra'),
    ('Face pull',                'TRAPEZIO',  'Puxada de corda na polia alta em direção ao rosto, trabalhando trapézio e deltoide posterior.', 'Polia alta e corda'),
    ('Pular corda',              'CARDIO',    'Saltos contínuos girando a corda, em ritmo constante ou intervalado.', 'Corda de pular'),
    ('Remo (ergômetro)',         'CARDIO',    'Remada contínua no ergômetro, exercício cardiovascular de corpo inteiro.', 'Remo ergômetro');
