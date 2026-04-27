package co.clinic.appointment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_KEY_SCHEME = "ApiKeyAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clinic Appointment API")
                        .description("RESTful API for managing clinic appointments, patients, and practitioners. " +
                                "Implements Strategy + Factory notification pattern with full audit logging.")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(API_KEY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(API_KEY_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-KEY")));
    }
}
