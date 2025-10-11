package pairmate.review_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// name은 Eureka에 등록된 서비스 이름 (store-service의 spring.application.name)으로 어노테이션을 달아줘야 해요
@FeignClient(name = "store-service")
public interface StoreClient {
    @GetMapping("/stores/{storeId}")
    StoreResponse getStoreById(@PathVariable("storeId") Long storeId);
}
