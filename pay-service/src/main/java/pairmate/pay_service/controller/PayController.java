package pairmate.pay_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.SuccessCode;
import pairmate.pay_service.dto.PayDTO;
import pairmate.pay_service.service.PayService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pay")
public class PayController {

    private final PayService payService;

    /**
     * 결제 요청
     */
    @PostMapping("/register")
    public ApiResponse<Void> registerPayment(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody PayDTO.PayRequestDTO request
    ) {
        payService.registerPayment(userId, request);
        return ApiResponse.onSuccess(null, SuccessCode.OK);
    }

}
