package com.akraness.akranesswaitlist.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Waiting List API Doc", version = "v1"),
servers = {
        @Server(
                description = "PROD ENV",
                url="https://user-service-dev.akranex.com/user"
        ),
        @Server(
                description = "LOCAL ENV",
                url = "http://localhost:8083/user"
        )
})
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class ApiDocConfig {
}
