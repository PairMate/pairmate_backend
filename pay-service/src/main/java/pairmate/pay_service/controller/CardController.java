package pairmate.pay_service.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.pay_service.dto.CardDTO;
import pairmate.pay_service.service.CardService;

import java.util.List;

@RestController
@RequestMapping("/api/pay/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    /**
     * 카드 등록
     */
    @PostMapping("/register")
    public ApiResponse<CardDTO.CardResponseDTO>registerCard(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CardDTO.CardRequestDTO request) {
        CardDTO.CardResponseDTO response = cardService.registerCard(userId, request);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }

    /**
     * 카드 목록 조회
     */
    @GetMapping("/list")
    public ApiResponse<List<CardDTO.CardResponseDTO>> getCardList(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId) {
        List<CardDTO.CardResponseDTO> response = cardService.getCardList(userId);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }

    /**
     * 카드 단건 조회
     */
    @GetMapping("/{cardId}")
    public ApiResponse<CardDTO.CardResponseDTO> getCard(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cardId) {
        CardDTO.CardResponseDTO response = cardService.getCard(cardId, userId);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }

    /**
     * 일일 한도 등록
     */
    @PatchMapping("/limit/{cardId}")
    public ApiResponse<String> registerDailyLimit(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cardId,
            @Valid @RequestBody CardDTO.DailyLimitRequestDTO request) {
        cardService.registerDailyLimit(userId, cardId,request);
        return ApiResponse.onSuccess("일일 한도 설정에 성공했습니다.", SuccessCode.OK);
    }

    /**
     * 카드 잔액 조회
     */
    @GetMapping("/cash/{cardId}")
    public ApiResponse<CardDTO.CardCashResponseDTO> getCardBalance(
            @Parameter(hidden = true) @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long cardId
    ) {
        CardDTO.CardCashResponseDTO response = cardService.getCardBalance(userId, cardId);
        return ApiResponse.onSuccess(response, SuccessCode.OK);
    }

}
