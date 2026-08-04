-- Dietas-modelo do sistema (usuario_id NULL = visível a todos os usuários).

INSERT INTO dietas (
    usuario_id, nome, origem, ativa, calculo_automatico,
    meta_calorias_kcal, meta_proteina_g, meta_carboidrato_g, meta_gordura_g, meta_agua_ml
) VALUES
    (NULL, 'Dieta Emagrecimento Padrão', 'TEMPLATE', FALSE, FALSE, 1800, 140, 150, 55, 2500),
    (NULL, 'Dieta Hipertrofia Padrão',   'TEMPLATE', FALSE, FALSE, 2800, 180, 320, 80, 3000),
    (NULL, 'Dieta Manutenção Padrão',    'TEMPLATE', FALSE, FALSE, 2200, 150, 240, 65, 2700);

INSERT INTO refeicoes (dieta_id, nome, ordem, horario_sugerido)
SELECT d.id, x.refeicao_nome, x.ordem, x.horario::TIME
FROM dietas d
JOIN (VALUES
    ('Dieta Emagrecimento Padrão', 'Café da manhã',    1, '07:00'),
    ('Dieta Emagrecimento Padrão', 'Almoço',            2, '12:00'),
    ('Dieta Emagrecimento Padrão', 'Lanche da tarde',   3, '16:00'),
    ('Dieta Emagrecimento Padrão', 'Jantar',            4, '20:00'),
    ('Dieta Hipertrofia Padrão',   'Café da manhã',    1, '07:00'),
    ('Dieta Hipertrofia Padrão',   'Almoço',            2, '12:00'),
    ('Dieta Hipertrofia Padrão',   'Pré-treino',        3, '15:30'),
    ('Dieta Hipertrofia Padrão',   'Pós-treino',        4, '18:30'),
    ('Dieta Hipertrofia Padrão',   'Jantar',            5, '21:00'),
    ('Dieta Manutenção Padrão',    'Café da manhã',    1, '07:00'),
    ('Dieta Manutenção Padrão',    'Almoço',            2, '12:00'),
    ('Dieta Manutenção Padrão',    'Lanche da tarde',   3, '16:00'),
    ('Dieta Manutenção Padrão',    'Jantar',            4, '20:00')
) AS x(dieta_nome, refeicao_nome, ordem, horario)
    ON x.dieta_nome = d.nome
WHERE d.origem = 'TEMPLATE' AND d.usuario_id IS NULL;

INSERT INTO refeicoes_alimentos (refeicao_id, alimento_id, quantidade_gramas, ordem)
SELECT r.id, a.id, x.quantidade, x.ordem
FROM refeicoes r
JOIN dietas d ON d.id = r.dieta_id
JOIN (VALUES
    -- Dieta Emagrecimento Padrão
    ('Dieta Emagrecimento Padrão', 'Café da manhã',  'Ovo de galinha cozido',        100, 1),
    ('Dieta Emagrecimento Padrão', 'Café da manhã',  'Pão integral',                  50, 2),
    ('Dieta Emagrecimento Padrão', 'Café da manhã',  'Mamão papaia',                 150, 3),
    ('Dieta Emagrecimento Padrão', 'Almoço',          'Peito de frango grelhado',     150, 1),
    ('Dieta Emagrecimento Padrão', 'Almoço',          'Arroz integral cozido',        100, 2),
    ('Dieta Emagrecimento Padrão', 'Almoço',          'Feijão carioca cozido',        100, 3),
    ('Dieta Emagrecimento Padrão', 'Almoço',          'Brócolis cozido',              100, 4),
    ('Dieta Emagrecimento Padrão', 'Lanche da tarde', 'Iogurte grego natural',        170, 1),
    ('Dieta Emagrecimento Padrão', 'Lanche da tarde', 'Banana prata',                 100, 2),
    ('Dieta Emagrecimento Padrão', 'Jantar',          'Filé de tilápia grelhado',     150, 1),
    ('Dieta Emagrecimento Padrão', 'Jantar',          'Batata-doce cozida',           150, 2),
    ('Dieta Emagrecimento Padrão', 'Jantar',          'Alface',                        50, 3),
    ('Dieta Emagrecimento Padrão', 'Jantar',          'Azeite de oliva extra virgem',  10, 4),
    -- Dieta Hipertrofia Padrão
    ('Dieta Hipertrofia Padrão', 'Café da manhã', 'Aveia em flocos',                  80, 1),
    ('Dieta Hipertrofia Padrão', 'Café da manhã', 'Leite integral',                  250, 2),
    ('Dieta Hipertrofia Padrão', 'Café da manhã', 'Banana prata',                    120, 3),
    ('Dieta Hipertrofia Padrão', 'Café da manhã', 'Pasta de amendoim integral',       20, 4),
    ('Dieta Hipertrofia Padrão', 'Almoço',         'Carne bovina patinho grelhado',  180, 1),
    ('Dieta Hipertrofia Padrão', 'Almoço',         'Arroz branco cozido',            200, 2),
    ('Dieta Hipertrofia Padrão', 'Almoço',         'Feijão carioca cozido',          100, 3),
    ('Dieta Hipertrofia Padrão', 'Almoço',         'Cenoura crua',                    80, 4),
    ('Dieta Hipertrofia Padrão', 'Pré-treino',     'Tapioca',                          80, 1),
    ('Dieta Hipertrofia Padrão', 'Pré-treino',     'Mel',                              15, 2),
    ('Dieta Hipertrofia Padrão', 'Pós-treino',     'Whey protein (pó)',                35, 1),
    ('Dieta Hipertrofia Padrão', 'Pós-treino',     'Banana prata',                    100, 2),
    ('Dieta Hipertrofia Padrão', 'Jantar',         'Salmão grelhado',                 180, 1),
    ('Dieta Hipertrofia Padrão', 'Jantar',         'Batata-doce cozida',              200, 2),
    ('Dieta Hipertrofia Padrão', 'Jantar',         'Espinafre cozido',                100, 3),
    -- Dieta Manutenção Padrão
    ('Dieta Manutenção Padrão', 'Café da manhã',  'Iogurte natural integral',        170, 1),
    ('Dieta Manutenção Padrão', 'Café da manhã',  'Granola',                          40, 2),
    ('Dieta Manutenção Padrão', 'Café da manhã',  'Maçã',                            130, 3),
    ('Dieta Manutenção Padrão', 'Almoço',          'Peito de frango grelhado',        150, 1),
    ('Dieta Manutenção Padrão', 'Almoço',          'Arroz branco cozido',             150, 2),
    ('Dieta Manutenção Padrão', 'Almoço',          'Lentilha cozida',                 100, 3),
    ('Dieta Manutenção Padrão', 'Almoço',          'Tomate',                           80, 4),
    ('Dieta Manutenção Padrão', 'Lanche da tarde', 'Queijo cottage',                  100, 1),
    ('Dieta Manutenção Padrão', 'Lanche da tarde', 'Pão integral',                     50, 2),
    ('Dieta Manutenção Padrão', 'Jantar',          'Carne suína lombo grelhado',      150, 1),
    ('Dieta Manutenção Padrão', 'Jantar',          'Mandioca cozida',                 150, 2),
    ('Dieta Manutenção Padrão', 'Jantar',          'Alface',                           50, 3)
) AS x(dieta_nome, refeicao_nome, alimento_nome, quantidade, ordem)
    ON x.dieta_nome = d.nome AND x.refeicao_nome = r.nome
JOIN alimentos a ON a.nome = x.alimento_nome
WHERE d.origem = 'TEMPLATE' AND d.usuario_id IS NULL;
