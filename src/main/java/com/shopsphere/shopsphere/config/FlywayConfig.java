package com.shopsphere.shopsphere.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration tells Spring: this class contains beans to register
@Configuration
public class FlywayConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    // @Bean tells Spring to manage this object
    // Spring will run this automatically on startup
    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource(url, username, password)
                .locations("classpath:db/migration")
                .baselineOnMigrate(false)
                .load();
    }
}