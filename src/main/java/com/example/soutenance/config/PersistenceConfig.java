package com.example.soutenance.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class PersistenceConfig {
    @Bean
    public PropertiesFactoryBean jpaProperties() {
        PropertiesFactoryBean factory = new PropertiesFactoryBean();
        factory.setProperties(new Properties() {{
            setProperty("hibernate.show_sql", "true");
            setProperty("hibernate.format_sql", "true");
            setProperty("hibernate.use_sql_comments", "true");
        }});
        return factory;
    }
}