package com.core.hmcts.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().
                        addList("Bearer Authentication"))
                .info(new Info().title("DTS Developer Technical Test")
                        .description("HMCTS requires a new system to be developed so caseworkers can keep track of their tasks. Your technical test is to develop that new system so caseworkers can efficiently manage their tasks.")
                        .version("1.0").contact(new Contact().name("Samson Ilemobayo")
                                .email("iilemobayosamson@gmail.com").url("#"))
                        .license(new License().name("License of API")
                                .url("API license URL")));
    }
}
