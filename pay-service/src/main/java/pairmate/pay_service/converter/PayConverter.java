package pairmate.pay_service.converter;

import org.springframework.stereotype.Component;
import pairmate.pay_service.domain.Foodlogs;
import pairmate.pay_service.dto.PayDTO;

@Component
public class PayConverter {
    /**
     * 결제 요청용 DTO -> Foodlogs 엔티티 변환
     * (결제 완료된 거래 기록으로 저장)
     */
    public Foodlogs toEntity(PayDTO.PayRequestDTO request, Long userId) {
        return Foodlogs.builder()
                .userId(userId)
                .storeId(request.getStoreId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .usedAt(request.getUsedAt())
                .status("COMPLETED")
                .build();
    }

    /**
     * 결제 없이 사용 내역만 기록하는 경우 (예: 잔액 차감만, 오프라인 입력)
     */

    public Foodlogs toEntityWithOutPayment(PayDTO.CardUsageRequestDTO request, Long userId) {
        return Foodlogs.builder()
                .userId(userId)
                .storeId(request.getStoreId())
                .amount(request.getAmount())
                .usedAt(request.getUsedAt())
                .status("PENDING")
                .build();
    }
