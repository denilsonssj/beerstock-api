package br.com.beerstock.beerstockapi.api.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Value("${database.config.url}")
    private String url;

    @Value("${database.config.username}")
    private String username;

    @Value("${database.config.password}")
    private String password;

    @Value("${database.config.driverClassname}")
    private String driverClassName;

    @Bean
    public DataSource getDataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(this.url);
        dataSourceBuilder.username(this.username);
        dataSourceBuilder.password(this.password);
        dataSourceBuilder.driverClassName(this.driverClassName);
        return dataSourceBuilder.build();
    }

}
