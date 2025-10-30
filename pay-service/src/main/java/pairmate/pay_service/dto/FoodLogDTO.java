package pairmate.pay_service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        private List<MenuResponseDTO> menus;
    }

    @Getter
    @NoArgsConstructor
    public static class MenuResponseDTO {
        private Long menuId;
        private String menuName;
        private Integer price;
        private String category;
    }

    @Getter
    public static class CardUsageRequestDTO {
        private String storeName;
        private Integer price;
        private LocalDate usedAt;
    }

}
