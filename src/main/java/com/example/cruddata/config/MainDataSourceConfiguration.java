package com.example.cruddata.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.core.env.Environment;


import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.cruddata.entity",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef= "transactionManager")
public class MainDataSourceConfiguration {

    @Autowired
    private Environment env;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties mainDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.configuration")
    public DataSource mainDataSource() {
        return mainDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mainEntityManagerFactory(EntityManagerFactoryBuilder builder) {


        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean=  builder.dataSource(mainDataSource()).packages("com.example.cruddata.entity").build();
        localContainerEntityManagerFactoryBean.setJpaProperties(additionalProperties());
        return localContainerEntityManagerFactoryBean;
    }
    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
//        properties.setProperty("hibernate.current_session_context_class", env.getProperty("spring.jpa.properties.hibernate.current_session_context_class"));
//        properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", env.getProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation"));
        properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));
        properties.setProperty("hibernate.format_sql", env.getProperty("spring.jpa.properties.hibernate.format_sql"));
        properties.setProperty("hibernate.physical_naming_strategy", env.getProperty("spring.jpa.hibernate.naming.physical-strategy"));
        return properties;
    }
    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager mainTransactionManager(
            final @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean mainEntityManagerFactory) {
        return new JpaTransactionManager(mainEntityManagerFactory.getObject());
    }
}
