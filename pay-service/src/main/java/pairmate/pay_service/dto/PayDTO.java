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
        private List<Long> menuIds;
        private LocalDate validUntil;
    }

    @Getter
    public static class CardUsageRequestDTO {
        private String storeName;
        private Integer price;
        private LocalDate usedAt;
    }
}
