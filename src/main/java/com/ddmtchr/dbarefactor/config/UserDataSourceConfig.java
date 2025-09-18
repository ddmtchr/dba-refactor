package com.ddmtchr.dbarefactor.config;

import com.arjuna.ats.internal.jdbc.drivers.PropertyFileDynamicClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.SQLException;

@Configuration
public class UserDataSourceConfig {

    @Bean
    public DataSource userDataSource() throws SQLException {
        var propertyFileDynamicClass = new PropertyFileDynamicClass();
        String file = this.getClass().getClassLoader().getResource("narayana-user.properties").getFile();
        XADataSource xaDataSource = propertyFileDynamicClass.getDataSource(file);
        return new XADataSourceWrapper(xaDataSource);
    }
}
