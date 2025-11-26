-- Script para criar a tabela saved_opportunities no PostgreSQL
-- Execute este script no seu banco de dados se a tabela não foi criada automaticamente

-- Criar tabela saved_opportunities
CREATE TABLE IF NOT EXISTS saved_opportunities (
    id BIGSERIAL PRIMARY KEY,
    athlete_id BIGINT NOT NULL,
    opportunity_id BIGINT NOT NULL,
    saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_saved_opportunities_athlete FOREIGN KEY (athlete_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_opportunities_opportunity FOREIGN KEY (opportunity_id) REFERENCES opportunities(id) ON DELETE CASCADE,
    CONSTRAINT uk_saved_opportunities_athlete_opportunity UNIQUE (athlete_id, opportunity_id)
);

-- Criar índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_saved_opportunities_athlete
    ON saved_opportunities(athlete_id);

CREATE INDEX IF NOT EXISTS idx_saved_opportunities_opportunity
    ON saved_opportunities(opportunity_id);

CREATE INDEX IF NOT EXISTS idx_saved_opportunities_saved_at
    ON saved_opportunities(saved_at DESC);

-- Comentários
COMMENT ON TABLE saved_opportunities IS 'Tabela que armazena as oportunidades salvas pelos atletas';
COMMENT ON COLUMN saved_opportunities.id IS 'ID único do registro';
COMMENT ON COLUMN saved_opportunities.athlete_id IS 'ID do atleta que salvou';
COMMENT ON COLUMN saved_opportunities.opportunity_id IS 'ID da oportunidade salva';
COMMENT ON COLUMN saved_opportunities.saved_at IS 'Data e hora em que foi salva';

