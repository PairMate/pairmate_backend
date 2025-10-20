package pairmate.pay_service.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class SelectionRequest {
    // 사용자가 선택한 아이템들의 ID 목록
    private List<Long> itemIds;
}