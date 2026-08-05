-- Catálogo de meditações guiadas do sistema.

INSERT INTO meditacoes
    (nome, tipo, duracao_minutos, descricao, instrucoes, inspirar_segundos, segurar_segundos, expirar_segundos)
VALUES
    ('Respiração 4-7-8', 'RESPIRACAO', 5,
     'Técnica clássica para reduzir a ansiedade e facilitar o sono.',
     'Sente-se ou deite-se confortavelmente. Inspire pelo nariz, segure o ar e expire lentamente pela boca, seguindo o ritmo indicado. Repita até o tempo terminar.',
     4, 7, 8),

    ('Respiração Calma', 'RESPIRACAO', 5,
     'Respiração equilibrada para acalmar a mente rapidamente.',
     'Inspire, segure e expire no mesmo ritmo, em ciclos contínuos. Foque apenas na contagem e no ar entrando e saindo.',
     4, 4, 4),

    ('Respiração Profunda', 'RESPIRACAO', 10,
     'Respiração lenta e profunda para reduzir o estresse.',
     'Inspire profundamente pelo abdômen, segure por um instante e solte o ar bem devagar. Mantenha os ombros relaxados.',
     5, 2, 5),

    ('Relaxamento Corporal', 'RELAXAMENTO', 10,
     'Body scan guiado, dos pés à cabeça.',
     'Feche os olhos. Leve a atenção aos pés, depois pernas, abdômen, peito, braços, ombros, pescoço e rosto, relaxando cada parte antes de seguir para a próxima.',
     NULL, NULL, NULL),

    ('Relaxamento Muscular Progressivo', 'RELAXAMENTO', 15,
     'Tensione e solte cada grupo muscular para liberar tensão acumulada.',
     'Contraia cada grupo muscular por alguns segundos e depois solte completamente, notando a diferença entre tensão e relaxamento. Vá dos pés até a cabeça.',
     NULL, NULL, NULL),

    ('Foco no Presente', 'FOCO', 8,
     'Traz a atenção de volta ao momento presente.',
     'Observe sua respiração sem tentar controlá-la. Sempre que a mente divagar, gentilmente volte a atenção para o ar entrando e saindo.',
     NULL, NULL, NULL),

    ('Concentração Antes do Treino', 'FOCO', 5,
     'Prepara a mente para o foco físico antes de treinar.',
     'Visualize o treino que está prestes a fazer. Respire fundo algumas vezes e direcione sua energia e atenção para o que vem a seguir.',
     NULL, NULL, NULL),

    ('Preparação para o Sono', 'SONO', 12,
     'Desacelera a mente e o corpo antes de dormir.',
     'Deite-se, escureça o ambiente e respire lentamente. Solte cada pensamento do dia, sem julgá-los, apenas deixando-os passar.',
     4, 4, 6),

    ('Alívio da Ansiedade', 'ANSIEDADE', 7,
     'Ajuda a acalmar picos de ansiedade.',
     'Respire fundo e devagar. Nomeie mentalmente 5 coisas que você vê, 4 que você ouve e 3 que você sente, trazendo a mente de volta ao presente.',
     4, 4, 6);
