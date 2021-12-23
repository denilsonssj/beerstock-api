package br.com.beerstock.beerstockapi.api.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfiguration {

    @Value("${springdoc.version}")
    private String SPRINGDOC_VERSION;

    private static final String BASE_PACKAGE = "br.com.beerstock.beerstockapi.api.controllers";
    private static final String API_TITLE = "Beer Stock API";
    private static final String API_DESCRIPTION = "REST API for beer stock management";
    private static final String CONTACT_NAME = "Denilson Santos Sobrinho Júnior";
    private static final String CONTACT_GITHUB = "https://github.com/denilsonssj";
    private static final String CONTACT_EMAIL = "dssjunior98@gmail.com";
    private static final String LICENSE_NAME = "MIT";
    private static final String LICENSE_URL = "https://raw.githubusercontent.com/denilsonssj/beerstockapi/main/LICENSE";

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
            .group("spring-public")
            .pathsToMatch("/api/v1/**")
            .packagesToScan(BASE_PACKAGE)
            .build();
    }

    @Bean
    public OpenAPI springBeerStockOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(SPRINGDOC_VERSION)
                .contact(new Contact()
                    .name(CONTACT_NAME)
                    .email(CONTACT_EMAIL)
                    .url(CONTACT_GITHUB))
                .license(new License()
                    .name(LICENSE_NAME)
                    .url(LICENSE_URL)));
    }

}
