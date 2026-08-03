-- Treinos-modelo do sistema (usuario_id NULL = visível a todos os usuários).

INSERT INTO treinos (usuario_id, nome, origem) VALUES
    (NULL, 'Treino A - Peito e Tríceps', 'TEMPLATE'),
    (NULL, 'Treino B - Costas e Bíceps', 'TEMPLATE'),
    (NULL, 'Treino C - Pernas', 'TEMPLATE');

INSERT INTO treino_exercicios (treino_id, exercicio_id, ordem, series_alvo, repeticoes_alvo, carga_alvo_kg, descanso_segundos)
SELECT t.id, e.id, x.ordem, x.series_alvo, x.repeticoes_alvo, NULL, x.descanso_segundos
FROM treinos t
JOIN (VALUES
    ('Treino A - Peito e Tríceps', 'Supino reto', 1, 4, 10, 90),
    ('Treino A - Peito e Tríceps', 'Supino inclinado', 2, 3, 12, 90),
    ('Treino A - Peito e Tríceps', 'Crucifixo', 3, 3, 12, 60),
    ('Treino A - Peito e Tríceps', 'Tríceps corda', 4, 3, 12, 60),
    ('Treino B - Costas e Bíceps', 'Puxada frontal', 1, 4, 10, 90),
    ('Treino B - Costas e Bíceps', 'Remada curvada', 2, 3, 10, 90),
    ('Treino B - Costas e Bíceps', 'Remada baixa', 3, 3, 12, 60),
    ('Treino B - Costas e Bíceps', 'Rosca direta', 4, 3, 12, 60),
    ('Treino C - Pernas', 'Agachamento livre', 1, 4, 10, 120),
    ('Treino C - Pernas', 'Leg press', 2, 4, 12, 90),
    ('Treino C - Pernas', 'Cadeira extensora', 3, 3, 15, 60),
    ('Treino C - Pernas', 'Cadeira flexora', 4, 3, 15, 60),
    ('Treino C - Pernas', 'Panturrilha em pé', 5, 4, 15, 45)
) AS x(treino_nome, exercicio_nome, ordem, series_alvo, repeticoes_alvo, descanso_segundos)
    ON x.treino_nome = t.nome
JOIN exercicios e ON e.nome = x.exercicio_nome
WHERE t.origem = 'TEMPLATE' AND t.usuario_id IS NULL;
