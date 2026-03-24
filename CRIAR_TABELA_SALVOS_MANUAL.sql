-- ============================================
-- CRIAR TABELA SAVED_OPPORTUNITIES
-- Execute este SQL no seu banco PostgreSQL
-- ============================================

-- 1. Deletar tabela se já existir (para recriar limpa)
DROP TABLE IF EXISTS saved_opportunities CASCADE;

-- 2. Criar tabela saved_opportunities
CREATE TABLE saved_opportunities (
    id BIGSERIAL PRIMARY KEY,
    athlete_id BIGINT NOT NULL,
    opportunity_id BIGINT NOT NULL,
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys (CORRIGIDO: tb_user e opportunity)
    CONSTRAINT fk_saved_opportunities_athlete
        FOREIGN KEY (athlete_id)
        REFERENCES tb_user(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_saved_opportunities_opportunity
        FOREIGN KEY (opportunity_id)
        REFERENCES opportunity(id)
        ON DELETE CASCADE,

    -- Constraint única (atleta não pode salvar a mesma oportunidade 2x)
    CONSTRAINT uk_saved_opportunities_athlete_opportunity
        UNIQUE (athlete_id, opportunity_id)
);

-- 3. Criar índices para performance
CREATE INDEX idx_saved_opportunities_athlete
    ON saved_opportunities(athlete_id);

CREATE INDEX idx_saved_opportunities_opportunity
    ON saved_opportunities(opportunity_id);

CREATE INDEX idx_saved_opportunities_saved_at
    ON saved_opportunities(saved_at DESC);

-- 4. Comentários (documentação)
COMMENT ON TABLE saved_opportunities IS
    'Armazena as oportunidades salvas (favoritas) pelos atletas';

COMMENT ON COLUMN saved_opportunities.id IS
    'ID único do registro';

COMMENT ON COLUMN saved_opportunities.athlete_id IS
    'ID do atleta que salvou a oportunidade';

COMMENT ON COLUMN saved_opportunities.opportunity_id IS
    'ID da oportunidade que foi salva';

COMMENT ON COLUMN saved_opportunities.saved_at IS
    'Data e hora em que a oportunidade foi salva';

-- 5. Verificar se foi criada
SELECT
    'Tabela criada com sucesso!' as status,
    COUNT(*) as total_registros
FROM saved_opportunities;

-- 6. Ver estrutura da tabela
\d saved_opportunities

