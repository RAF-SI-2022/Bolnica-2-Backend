package com.raf.si.patientservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    public final String APP_TITLE = "Patient Service";
    public final String APP_DESCRIPTION = "API for patient service";
    public final String APP_API_VERSION = "1.0";
    public final String APP_LICENSE = "license";
    public final String APP_LICENSE_URL = "url";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title(APP_TITLE)
                        .description(APP_DESCRIPTION)
                        .version(APP_API_VERSION)
                        .license(new License().name(APP_LICENSE).url(APP_LICENSE_URL)));
    }
}
