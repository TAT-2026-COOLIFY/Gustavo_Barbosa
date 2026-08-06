-- Treinos-modelo adicionais do sistema (usuario_id NULL = visível a todos os usuários).
-- Para exercícios de cardio, "séries" e "repetições" são reaproveitados como
-- "intervalos" e "minutos por intervalo" (o schema não tem um campo de duração dedicado).

INSERT INTO treinos (usuario_id, nome, origem) VALUES
    (NULL, 'Treino D - Ombro', 'TEMPLATE'),
    (NULL, 'Treino E - Antebraço', 'TEMPLATE'),
    (NULL, 'Treino F - Trapézio', 'TEMPLATE'),
    (NULL, 'Treino G - Cardio', 'TEMPLATE');

INSERT INTO treino_exercicios (treino_id, exercicio_id, ordem, series_alvo, repeticoes_alvo, carga_alvo_kg, descanso_segundos)
SELECT t.id, e.id, x.ordem, x.series_alvo, x.repeticoes_alvo, NULL, x.descanso_segundos
FROM treinos t
JOIN (VALUES
    ('Treino D - Ombro', 'Desenvolvimento militar', 1, 4, 10, 90),
    ('Treino D - Ombro', 'Elevação lateral',        2, 3, 12, 60),
    ('Treino D - Ombro', 'Elevação frontal',        3, 3, 12, 60),
    ('Treino D - Ombro', 'Elevação posterior',      4, 3, 15, 60),
    ('Treino E - Antebraço', 'Rosca de punho',           1, 3, 15, 45),
    ('Treino E - Antebraço', 'Rosca de punho invertida', 2, 3, 15, 45),
    ('Treino E - Antebraço', 'Farmer''s walk',           3, 3, 1,  60),
    ('Treino F - Trapézio', 'Encolhimento de ombros', 1, 4, 15, 60),
    ('Treino F - Trapézio', 'Remada alta',            2, 3, 12, 60),
    ('Treino F - Trapézio', 'Face pull',              3, 3, 15, 60),
    ('Treino G - Cardio', 'Esteira',                1, 1, 20, 60),
    ('Treino G - Cardio', 'Bicicleta ergométrica',  2, 1, 20, 60),
    ('Treino G - Cardio', 'Pular corda',            3, 3, 5,  60),
    ('Treino G - Cardio', 'Remo (ergômetro)',       4, 1, 15, 60)
) AS x(treino_nome, exercicio_nome, ordem, series_alvo, repeticoes_alvo, descanso_segundos)
    ON x.treino_nome = t.nome
JOIN exercicios e ON e.nome = x.exercicio_nome
WHERE t.origem = 'TEMPLATE' AND t.usuario_id IS NULL;
