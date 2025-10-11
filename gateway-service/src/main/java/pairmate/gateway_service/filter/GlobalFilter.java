package pairmate.gateway_service.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (config.isPreLogger()) {
                log.info("[GlobalFilter PRE] {} {}", request.getMethod(), request.getURI());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("[GlobalFilter POST] status -> {}", response.getStatusCode());
                }
            }));
        };
    }

    public static class Config {
        private boolean preLogger;
        private boolean postLogger;

        public boolean isPreLogger() { return preLogger; }
        public void setPreLogger(boolean preLogger) { this.preLogger = preLogger; }
        public boolean isPostLogger() { return postLogger; }
        public void setPostLogger(boolean postLogger) { this.postLogger = postLogger; }
    }
}
