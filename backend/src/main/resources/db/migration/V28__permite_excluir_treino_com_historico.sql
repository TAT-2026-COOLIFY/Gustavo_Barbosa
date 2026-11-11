-- Excluir um treino que já foi executado falhava por FK: sessoes_treino e series_executadas
-- referenciavam o treino/seus exercícios sem regra de exclusão.

-- Excluir o treino leva junto as sessões dele (e, via V10, as séries de cada sessão).
ALTER TABLE sessoes_treino DROP CONSTRAINT sessoes_treino_treino_id_fkey;
ALTER TABLE sessoes_treino
    ADD CONSTRAINT sessoes_treino_treino_id_fkey FOREIGN KEY (treino_id) REFERENCES treinos (id) ON DELETE CASCADE;

-- Editar um treino recria seus treino_exercicios; SET NULL preserva as séries já executadas
-- (o volume e o histórico só dependem da sessão) em vez de bloquear a edição ou apagar o histórico.
ALTER TABLE series_executadas ALTER COLUMN treino_exercicio_id DROP NOT NULL;
ALTER TABLE series_executadas DROP CONSTRAINT series_executadas_treino_exercicio_id_fkey;
ALTER TABLE series_executadas
    ADD CONSTRAINT series_executadas_treino_exercicio_id_fkey
        FOREIGN KEY (treino_exercicio_id) REFERENCES treino_exercicios (id) ON DELETE SET NULL;
