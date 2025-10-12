package pairmate.store_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuRequest {
    private String menuName;
    private Integer menuPrice;
}
