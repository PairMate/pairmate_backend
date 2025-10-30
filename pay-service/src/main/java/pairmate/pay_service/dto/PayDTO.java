package pairmate.pay_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PayDTO {

    @Getter
    public static class PayRequestDTO {
        private Long userId;
        private String storeName;
        private String storeType;
        private String category;
        private List<MenuDTO> menuList;
        private LocalDate validUntil;
    }

    @Getter
    public static class MenuDTO {
        private Long menuId;
        private String menuName;
        private String price;
        private String count;  // 문자열 수량
    }


    @Getter
    public static class CardUsageRequestDTO {
        private String storeName;
        private Integer price;
        private LocalDate usedAt;
    }
}
