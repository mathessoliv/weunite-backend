@echo off
echo ================================================
echo   CRIANDO TABELA SAVED_OPPORTUNITIES
echo ================================================
echo.

REM Configurações do banco de dados
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=weunite
set DB_USER=postgres
set DB_PASSWORD=sua_senha

echo Conectando ao banco de dados...
echo.

REM Executar o script SQL
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "src\main\resources\create_saved_opportunities_table.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================
    echo   TABELA CRIADA COM SUCESSO!
    echo ================================================
) else (
    echo.
    echo ================================================
    echo   ERRO AO CRIAR TABELA
    echo ================================================
    echo.
    echo Verifique:
    echo 1. PostgreSQL está rodando
    echo 2. Credenciais estão corretas
    echo 3. Banco de dados 'weunite' existe
)

echo.
pause

