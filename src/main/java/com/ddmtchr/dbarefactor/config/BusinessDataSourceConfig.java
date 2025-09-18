package com.ddmtchr.dbarefactor.config;

import com.arjuna.ats.internal.jdbc.drivers.PropertyFileDynamicClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.SQLException;

@Configuration
public class BusinessDataSourceConfig {
    @Bean
    public DataSource businessDataSource() throws SQLException {
        var propertyFileDynamicClass = new PropertyFileDynamicClass();
        String file = this.getClass().getClassLoader().getResource("narayana-business.properties").getFile();
        XADataSource xaDataSource = propertyFileDynamicClass.getDataSource(file);
        return new XADataSourceWrapper(xaDataSource);
    }
}
