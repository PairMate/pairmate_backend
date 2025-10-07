package pairmate.pay_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi payApi() {
        return GroupedOpenApi.builder()
                .group("pay")
                .pathsToMatch("/api/pays/**")
                .build();
    }
}
