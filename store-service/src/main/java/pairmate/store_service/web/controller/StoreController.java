package pairmate.store_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pairmate.store_service.service.StoreService;
import pairmate.store_service.web.dto.MenuResponse;
import pairmate.store_service.web.dto.StoreRegisterRequest;
import pairmate.store_service.web.dto.StoreResponse;

import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/recommend")
    public List<StoreResponse> getRecommendedStores() {
        return storeService.getRecommendedStores();
    }

    @GetMapping("/{storeId}")
    public StoreResponse getStoreDetail(@PathVariable Long storeId) {
        return storeService.getStoreDetail(storeId);
    }

    @GetMapping("/{storeId}/menus")
    public List<MenuResponse> getStoreMenus(@PathVariable Long storeId) {
        return storeService.getStoreMenus(storeId);
    }

    @PostMapping
    public ResponseEntity<Void> registerStore(@RequestBody StoreRegisterRequest request) {
        storeService.registerStore(request); // 카테고리 조회 포함
        return ResponseEntity.ok().build();
    }

}