package com.ddmtchr.dbarefactor.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.ddmtchr.dbarefactor.security.repository",
                "com.ddmtchr.dbarefactor.repository"
        },
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "platformTransactionManager"
)
public class DataSourceConfig {
    @Value("${spring.jpa.properties.hibernate.transaction.jta.platform}")
    private String jtaPlatform;

    @Value("${spring.jpa.properties.hibernate.physical_naming_strategy}")
    private String physicalNamingStrategy;

    @Value("${spring.jpa.properties.hibernate.implicit_naming_strategy}")
    private String implicitNamingStrategy;

    @Value("${spring.jpa.properties.hibernate.show_sql}")
    private String showSql;

    @Value("${spring.jpa.properties.hibernate.format_sql}")
    private String formatSql;

    @Value("${spring.jpa.properties.hibernate.hbm2ddl.auto}")
    private String hbm2ddl;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;

    @Bean
    public DataSource routingDataSource(
            @Qualifier("userDataSource") DataSource userDataSource,
            @Qualifier("businessDataSource") DataSource businessDataSource) {

        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return null;
            }
        };

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("userDB", userDataSource);
        dataSourceMap.put("businessDB", businessDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(userDataSource);

        return routingDataSource;
    }

    @Bean
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/dbarefactor")
                .username("postgres")
                .password("postgres")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("routingDataSource") DataSource routingDataSource) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.transaction.jta.platform", jtaPlatform);
        jpaProperties.put("hibernate.physical_naming_strategy", physicalNamingStrategy);
        jpaProperties.put("hibernate.implicit_naming_strategy", implicitNamingStrategy);
        jpaProperties.put("hibernate.show_sql", showSql);
        jpaProperties.put("hibernate.format_sql", formatSql);
        jpaProperties.put("hibernate.hbm2ddl.auto", hbm2ddl);
        jpaProperties.put("hibernate.dialect", dialect);

        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJtaDataSource(routingDataSource);
        factory.setPackagesToScan("com.ddmtchr.dbarefactor.security.entity", "com.ddmtchr.dbarefactor.entity");
        factory.setJpaPropertyMap(jpaProperties);
        factory.setPersistenceUnitName("defaultPU");

        return factory;
    }

}
