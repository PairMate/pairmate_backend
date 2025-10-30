package pairmate.pay_service.converter;

import org.springframework.stereotype.Component;
import pairmate.pay_service.domain.FoodLogMenus;
import pairmate.pay_service.domain.Foodlogs;
import pairmate.pay_service.dto.PayDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PayConverter {

    /**
     * 결제 요청 DTO → Foodlogs 엔티티 변환
     */
    public Foodlogs toFoodlogsEntity(PayDTO.PayRequestDTO request, Long userId) {
        return Foodlogs.builder()
                .userId(userId)
                .storeName(request.getStoreName())
                .storeType(request.getStoreType())
                .category(request.getCategory())
                .isUsed(false)
                .usedAt(null)
                .build();
    }

    /**
     * 결제 없이 사용 내역만 기록하는 경우
     */
    public Foodlogs toFoodlogsEntityWithoutPayment(PayDTO.CardUsageRequestDTO request, Long userId) {
        return Foodlogs.builder()
                .userId(userId)
                .storeName(request.getStoreName())
                .storeType(null)
                .category(null)
                .isUsed(true)
                .usedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 메뉴 리스트 → FoodLogMenus 엔티티 리스트 변환
     */
    public List<FoodLogMenus> toFoodLogMenuEntities(Foodlogs foodlogs, List<PayDTO.MenuDTO> menuList) {
        return menuList.stream()
                .map(menu -> FoodLogMenus.builder()
                        .foodlogs(foodlogs)
                        .menuId(menu.getMenuId())
                        .build())
                .collect(Collectors.toList());
    }


}
