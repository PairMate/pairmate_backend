package pairmate.store_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 현재 요청의 HttpServletRequest 객체를 가져오기
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();

                    // 현재 요청(store-service로 들어온)의 Authorization 헤더 찾기
                    String authHeader = request.getHeader("Authorization");

                    if (authHeader != null && !authHeader.isEmpty()) {
                        // Feign 요청(user-service로 나가는)에 Authorization 헤더를 추가하기
                        template.header("Authorization", authHeader);
                    }

                    // X-User-Id 헤더도 전파가 필요하다면 추가할 수 있다
                    // (하지만 이 경우는 UserClient에서 이미 userId를 파라미터로 받고 있으므로 필요 없을 수 있음)
                    // String userIdHeader = request.getHeader("X-User-Id");
                    // if (userIdHeader != null) {
                    //     template.header("X-User-Id", userIdHeader);
                    // }
                }
            }
        };
    }
}