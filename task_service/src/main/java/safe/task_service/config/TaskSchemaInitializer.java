package safe.task_service.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Backward-compatible schema guard for existing local databases.
 */
@Configuration
public class TaskSchemaInitializer {

    @Bean
    ApplicationRunner ensureTaskColumns(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurring boolean");
            jdbcTemplate.execute("UPDATE tasks SET recurring = false WHERE recurring IS NULL");
            jdbcTemplate.execute("ALTER TABLE tasks ALTER COLUMN recurring SET DEFAULT false");
            jdbcTemplate.execute("ALTER TABLE tasks ALTER COLUMN recurring SET NOT NULL");
            jdbcTemplate.execute("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_interval integer");
            jdbcTemplate.execute("ALTER TABLE tasks ADD COLUMN IF NOT EXISTS recurrence_unit varchar(255)");
        };
    }
}
