package com.ddmtchr.dbarefactor.config;

import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.SystemException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Bean
    public UserTransactionManager userTransactionManager() throws SystemException {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setTransactionTimeout(300);
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    @Bean("transactionManager")
    public PlatformTransactionManager platformTransactionManager(UserTransactionManager userTransactionManager) {
        return new JtaTransactionManager(userTransactionManager, userTransactionManager);
    }
}
