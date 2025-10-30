package pairmate.pay_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TicketDTO {

    @Getter
    @AllArgsConstructor
    public static class TicketIssueRequest {
        private Long userId;
        private Long storeId;
        private String menuName;
        private Integer price;
    }

    @Getter
    public static class TicketIssueResponse {
        private Long ticketId;
        private String storeName;
        private String menuName;
        private Integer price;
        private LocalDateTime issuedAt;
    }

    @Getter
    public static class MyTicketResponse {
        private Long foodLogId;
        private String storeName;
        private String menuName;
        private Integer price;
        private String status;
        private String category;
        private String storeType;
        private LocalDate validUntil;
    }
}