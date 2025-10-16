package pairmate.store_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pairmate.common_libs.response.ApiResponse;
import pairmate.store_service.dto.UserResponseDto;

@FeignClient(name = "user-service") // Eureka에 등록된 user-service
public interface UserClient {

    @GetMapping("/users/internal/{userId}")
    ApiResponse<UserResponseDto> getUserById(@PathVariable("userId") Long userId);
}