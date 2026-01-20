package com.ddmtchr.dbarefactor.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.ddmtchr.dbarefactor.security.repository",
                "com.ddmtchr.dbarefactor.repository"
        },
        transactionManagerRef = "transactionManager"
)
public class DataSourceConfig {

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    @Value("${spring.jpa.properties.hibernate.physical_naming_strategy}")
    private String hibernatePhysicalNamingStrategy;

    @Value("${spring.jpa.properties.hibernate.implicit_naming_strategy}")
    private String hibernateImplicitNamingStrategy;

    @Bean
    public DataSource routingDataSource(
            @Qualifier("userDataSource") DataSource userDataSource,
            @Qualifier("businessDataSource") DataSource businessDataSource) {

        AbstractRoutingDataSource ds = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return null;
            }
        };

        Map<Object, Object> map = new HashMap<>();
        map.put("userDB", userDataSource);
        map.put("businessDB", businessDataSource);

        ds.setTargetDataSources(map);
        ds.setDefaultTargetDataSource(userDataSource);
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource routingDataSource) {

        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emf.setJtaDataSource(routingDataSource);
        emf.setPackagesToScan(
                "com.ddmtchr.dbarefactor.security.entity",
                "com.ddmtchr.dbarefactor.entity"
        );
        emf.setPersistenceUnitName("defaultPU");

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", hibernateDialect);
        props.put("hibernate.physical_naming_strategy", hibernatePhysicalNamingStrategy);
        props.put("hibernate.implicit_naming_strategy", hibernateImplicitNamingStrategy);

        emf.setJpaPropertyMap(props);
        return emf;
    }
}
