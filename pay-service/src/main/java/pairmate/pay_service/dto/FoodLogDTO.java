package pairmate.pay_service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pairmate.pay_service.domain.FoodLogMenus;
import pairmate.pay_service.domain.Foodlogs;

import java.time.LocalDate;
import java.util.List;

public class FoodLogDTO {

    @Getter
    @Builder
    public static class FoodLogResponseDTO {
        private Long foodLogId;
        private String storeName;
        private String storeType;
        private Boolean isUsed;
        private LocalDate validUntil;
        private String category;
        private List<MenuResponseDTO> menus;
    }

    @Getter
    @Builder
    public static class MenuResponseDTO {
        private Long menuId;
        private String menuName;
        private Integer price;
    }

    @Getter
    public static class CardUsageRequestDTO {
        private String storeName;
        private Integer price;
        private LocalDate usedAt;
    }

}
