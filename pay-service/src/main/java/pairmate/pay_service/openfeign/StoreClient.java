package pairmate.pay_service.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.response.ApiResponse;
import pairmate.pay_service.dto.StoreDTO;


@FeignClient(
        name = "store-service",       // Eureka serviceId
        path = "/api/stores"          // store-service의 공통 prefix
)
public interface StoreClient {

    /**
     * 가게 단건 조회
     */
    @GetMapping("/{storeId}")
    StoreDTO.StoreResponse getStore(@PathVariable("storeId") Long storeId);

    /**
     * 특정 가게의 메뉴 단건 조회
     */
    @GetMapping("/{storeId}/menus/{menuId}")
    ApiResponse<StoreDTO.MenuResponse> getMenu(
            @PathVariable("storeId") Long storeId,
            @PathVariable("menuId") Long menuId
    );


}
