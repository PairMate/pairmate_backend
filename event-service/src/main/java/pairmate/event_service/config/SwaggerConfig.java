package pairmate.event_service.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi eventApi() {
        return GroupedOpenApi.builder()
                .group("event")
                .pathsToMatch("/api/events/**")
                .build();
    }
}
