package pairmate.pay_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.pay_service.converter.PayConverter;
import pairmate.pay_service.domain.ChildCards;
import pairmate.pay_service.domain.FoodLogMenus;
import pairmate.pay_service.domain.Foodlogs;
import pairmate.pay_service.dto.FoodLogDTO;
import pairmate.pay_service.dto.PayDTO;
import pairmate.pay_service.openfeign.StoreClient;
import pairmate.pay_service.repository.ChildCardsRepository;
import pairmate.pay_service.repository.FoodLogMenusRepository;
import pairmate.pay_service.repository.FoodLogRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayService {

    private final FoodLogRepository foodLogsRepository;
    private final FoodLogMenusRepository foodLogMenusRepository;
    private final ChildCardsRepository childCardsRepository;
    private final PayConverter payConverter;
    private final StoreClient storeClient;

    /**
     * 메뉴 선택 및 결제
     * @param userId
     * @param request
     */
    @Transactional
    public FoodLogDTO.FoodLogResponseDTO registerPayment(Long userId, PayDTO.PayRequestDTO request) {
        // 0. 유저의 최근 등록 카드 조회
        ChildCards card = childCardsRepository.findTop1ByUserIdOrderByCardIdDesc(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));

        // 1. 메뉴 총 금액 계산 (프론트에서 전달받은 가격 + 수량 기반)
        int totalPrice = request.getMenuList().stream()
                .mapToInt(menu -> Integer.parseInt(menu.getPrice()) * Integer.parseInt(menu.getCount()))
                .sum();

        // 2. 잔액 검증
        if (card.getCash() == 0 || card.getCash() < totalPrice) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // 3. 일일 한도 검증
        if (card.getDayLimit() < totalPrice) {
            throw new CustomException(ErrorCode.CARD_LIMIT_EXCEEDED);
        }

        // 4. Foodlogs 생성 (가게 정보 포함, 기본 미사용 상태)
        Foodlogs foodlogs = payConverter.toFoodlogsEntity(request, userId);
        Foodlogs savedLog = foodLogsRepository.save(foodlogs);

        // 5. FoodLogMenus 생성 (여러 메뉴 선택 가능)
        List<FoodLogMenus> menuEntities = payConverter.toFoodLogMenuEntities(savedLog, request.getMenuList());
        foodLogMenusRepository.saveAll(menuEntities);

        // 6. 카드 잔액 차감
        card.updateCash(card.getCash() - totalPrice);

        // 7. 응답 DTO 생성
        List<FoodLogDTO.MenuResponseDTO> menus = menuEntities.stream()
                .map(menu -> FoodLogDTO.MenuResponseDTO.builder()
                        .menuId(menu.getMenuId())
                        .menuName(menu.getMenuName())
                        .price(menu.getPrice())
                        .build())
                .toList();

        // 8. 최종 반환 DTO
        return FoodLogDTO.FoodLogResponseDTO.builder()
                .foodLogId(savedLog.getFoodLogId())
                .storeName(savedLog.getStoreName())
                .storeType(savedLog.getStoreType())
                .isUsed(savedLog.getIsUsed())
                .validUntil(savedLog.getCreatedAt().toLocalDate().plusDays(7))
                .category(savedLog.getCategory())
                .menus(menus)
                .build();
    }

    /**
     * 티켓 목록 조회 (최대 6건)
     */
    @Transactional(readOnly = true)
    public List<FoodLogDTO.FoodLogResponseDTO> getRecentFoodLogs(Long userId) {
        // 1. 최근 6건의 Foodlogs 조회
        List<Foodlogs> logs = foodLogsRepository.findTop6ByUserIdOrderByCreatedAtDesc(userId);

        // 2. 각 로그를 DTO로 변환
        return logs.stream()
                .map(log -> {
                    // (1) 메뉴 리스트 변환
                    List<FoodLogDTO.MenuResponseDTO> menus = log.getMenus().stream()
                            .map(menu -> FoodLogDTO.MenuResponseDTO.builder()
                                    .menuId(menu.getMenuId())
                                    .menuName(menu.getMenuName())
                                    .price(menu.getPrice())
                                    .build())
                            .toList();

                    // (2) FoodLogResponseDTO 생성
                    return FoodLogDTO.FoodLogResponseDTO.builder()
                            .foodLogId(log.getFoodLogId())
                            .storeName(log.getStoreName())
                            .storeType(log.getStoreType())
                            .isUsed(log.getIsUsed())
                            .validUntil(log.getCreatedAt().toLocalDate().plusDays(7))
                            .category(log.getCategory())
                            .menus(menus)
                            .build();
                })
                .toList();
    }




}
