package pairmate.pay_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PayDTO {

    @Getter
    public static class PayRequestDTO {
        private Long storeId;
        private Long menuId;
        private String menuName;
        private Integer price;
    }

    @Getter
    @AllArgsConstructor
    public static class PayResponseDTO {
        private Long ticketId;
        private String storeName;
        private String menuName;
        private Integer price;
        private LocalDateTime paidAt;

        public PayResponseDTO(TicketDTO.TicketIssueResponse response) {
            this.ticketId = response.getTicketId();
            this.storeName = response.getStoreName();
            this.menuName = response.getMenuName();
            this.price = response.getPrice();
            this.paidAt = response.getIssuedAt();
        }
    }

    @Getter
    public static class CardUsageRequestDTO {
        private String storeName;
        private Integer price;
        private LocalDate usedAt;
    }
}
