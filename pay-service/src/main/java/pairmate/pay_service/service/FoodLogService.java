package pairmate.pay_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Store;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.pay_service.domain.Foodlogs;
import pairmate.pay_service.dto.FoodLogDTO;
import pairmate.pay_service.dto.StoreDTO;
import pairmate.pay_service.openfeign.StoreClient;
import pairmate.pay_service.repository.FoodLogRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FoodLogService {
    private final FoodLogRepository foodLogRepository;
    private final StoreClient storeClient;

    /**
     * 푸드로그를 사용완료 상태로 변경하는 메서드
     */
    public void markTicketAsUsed(Long userId, FoodLogDTO.FoodLogRequestDTO request) {
        // 푸드로그 조회
        Foodlogs foodlog = foodLogRepository.findByFoodLogId(request.getFoodLogId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "티켓 정보를 찾을 수 없습니다."));

        // 이미 사용된 푸드로그인지 확인
        if (Boolean.TRUE.equals(foodlog.getIsUsed())) {
            throw new CustomException(ErrorCode.INVALID_REQUEST, "이미 사용된 티켓입니다.");
        }

        Long menuId = foodlog.getMenuId();      // 음식 id 반환

        StoreDTO.MenuResponse menu;
        StoreDTO.StoreResponse store;

        try {
            menu = storeClient.getMenuInfo(menuId);
            if (menu == null || menu.getStoreId() == null) {
                throw new CustomException(ErrorCode.NOT_FOUND, "푸드로그에 연결된 메뉴 정보(가게 ID)를 찾을 수 없습니다.");
            }

            store = storeClient.getStore(menu.getStoreId());
            if (store == null || store.getUserId() == null) {
                throw new CustomException(ErrorCode.NOT_FOUND, "가게 소유주 정보를 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "메뉴/가게 정보 조회에 실패했습니다.");
        }

        if (!store.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "이 티켓을 처리할 권한이 없습니다. (가게 소유주가 아님)");
        }

        foodlog.markAsUsed();
    }
}
