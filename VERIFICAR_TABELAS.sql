-- ============================================
-- VERIFICAR TABELAS EXISTENTES NO BANCO
-- Execute este SQL PRIMEIRO para ver as tabelas
-- ============================================

-- 1. Ver todas as tabelas do banco
SELECT
    table_name,
    table_type
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- 2. Procurar tabelas que contenham "user" no nome
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name LIKE '%user%';

-- 3. Procurar tabelas que contenham "opportunity" no nome
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_name LIKE '%opportunit%';

-- 4. Ver todas as tabelas (simplificado)
\dt

