package pairmate.pay_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.pay_service.dto.FoodLogDTO;
import pairmate.pay_service.service.FoodLogService;

@RestController
@RequestMapping("/pay/tickets")
@RequiredArgsConstructor
public class FoodLogController {
    private final FoodLogService ticketService;
    /**
     * 티켓 사용완료 처리 기능
     */
    @Operation(summary = "티켓 사용완료 처리", description = "본인이 보유한 특정 티켓을 사용완료 상태로 변경합니다.")
    @PatchMapping("/set-used")
    public ApiResponse<String> setTicketAsUsed(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody FoodLogDTO.FoodLogRequestDTO request) {
        ticketService.markTicketAsUsed(userId, request);
        return ApiResponse.onSuccess("티켓이 사용완료 처리되었습니다.", SuccessCode.OK);
    }
}
