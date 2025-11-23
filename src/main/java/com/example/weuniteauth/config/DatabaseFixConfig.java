package com.example.weuniteauth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseFixConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixDatabaseConstraints() {
        try {
            // Tenta remover a constraint antiga que pode estar desatualizada
            // O nome 'report_status_check' é o padrão gerado pelo Hibernate/Postgres para validação de enum
            try {
                jdbcTemplate.execute("ALTER TABLE report DROP CONSTRAINT IF EXISTS report_status_check");
            } catch (Exception e) {
                // Ignora erro se a constraint não existir ou tiver outro nome
                System.out.println("Aviso: Não foi possível remover a constraint report_status_check (pode não existir): " + e.getMessage());
            }
            
            // Migra dados antigos se existirem para evitar violação da nova constraint
            try {
                jdbcTemplate.execute("UPDATE report SET status = 'RESOLVED', action_taken = 'NONE' WHERE status = 'DISMISSED'");
            } catch (Exception e) {
                System.out.println("Aviso: Erro ao migrar status DISMISSED: " + e.getMessage());
            }

            // Adiciona a nova constraint com todos os status permitidos
            // Isso permite que o banco aceite 'REVIEWED' e 'RESOLVED' e remove 'DISMISSED'
            jdbcTemplate.execute("ALTER TABLE report ADD CONSTRAINT report_status_check CHECK (status IN ('PENDING', 'RESOLVED', 'REVIEWED'))");
            
            System.out.println("Constraint 'report_status_check' atualizada com sucesso para incluir apenas PENDING, RESOLVED e REVIEWED.");
        } catch (Exception e) {
            System.err.println("Erro ao tentar atualizar constraint do banco de dados: " + e.getMessage());
            // Não lançamos a exceção para não impedir a inicialização da aplicação, 
            // mas o erro ficará no log se falhar.
        }
    }
}
