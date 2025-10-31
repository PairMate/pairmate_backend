package pairmate.pay_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pairmate.pay_service.domain.ChildCards;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CardDTO {

    /** 카드 등록 요청 DTO **/
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardRequestDTO {

        @NotBlank(message = "카드 번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^[0-9]{16}$", message = "카드 번호는 숫자 16자리여야 합니다.")
        private String cardNumber;

        @NotBlank(message = "CVC 번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^[0-9]{3}$", message = "CVC 번호는 숫자 3자리여야 합니다.")
        private String cvc;

        @NotNull(message = "유효기간은 필수 입력 항목입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate expireDate;

        @Min(value = 0, message = "일일 한도는 0 이상이어야 합니다.")
        private int dayLimit;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^[0-9]{2}$", message = "비밀번호는 숫자 2자리여야 합니다.")
        private String password;
    }

    /** 카드 응답 DTO **/
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardResponseDTO {
        private Long cardId;
        private String cardNumber;
        private String cvc;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate expireDate;
        private int dayLimit;
        private String password;
        private Long userId;
    }

    /** 카드 목록 응답 **/
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardListResponseDTO {
        private List<CardResponseDTO> cards;
    }

    /** 일일 한도 등록 요청 DTO **/
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyLimitRequestDTO {
        @NotNull(message = "한도 금액은 필수 입력 항목입니다.")
        @Min(value = 0, message = "유효하지 않은 한도 금액입니다.")
        private Integer dailyLimit;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CardCashResponseDTO {
        private int cash;
        public CardCashResponseDTO(ChildCards childCards) {
            this.cash = childCards.getDayLimit();
        }
    }

    /** 이번 달 & 오늘 결제 금액 조회 응답 DTO **/
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentRecordResponseDTO {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
        private YearMonth month;
        private int monthlyTotal;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate today;
        private int todayTotal;
    }


}