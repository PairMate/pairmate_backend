package pairmate.pay_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.pay_service.dto.PayDTO;
import pairmate.pay_service.openfeign.StoreClient;
import pairmate.pay_service.repository.FoodLogsRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PayService {

    private final FoodLogsRepository foodLogsRepository;
    private final StoreClient storeClient;

    /**
     * 이번 달 결제금액 조회
     */
    @Transactional(readOnly = true)
    public int getMonthlyPayment(Long userId) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
        return foodLogsRepository.sumPriceByUserAndPeriod(userId, start, end)
                .orElse(0);
    }

    /**
     * 오늘 결제금액 조회
     */
    @Transactional(readOnly = true)
    public int getTodayPayment(Long userId) {
        LocalDate today = LocalDate.now();
        return foodLogsRepository.sumPriceByUserAndDate(userId, today)
                .orElse(0);
    }

    // 메뉴 선택 및 결제
    @Transactional
    public void payByStoreAndMenu(Long userId, PayDTO.PayRequestDTO payRequestDTO) {

    }
    // 티켓 목록 조회 (최대 6건)
    // 티켓 사용완료 처리
    // 카드 사용내역 등록


}
