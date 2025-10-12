package pairmate.store_service.web.dto;

import lombok.Builder;
import lombok.Getter;
import pairmate.store_service.domain.Menus;

@Getter
@Builder
public class MenuResponse {
    private Long menuId;
    private String menuName;
    private Integer menuPrice;

    public static MenuResponse fromEntity(Menus entity) {
        return MenuResponse.builder()
                .menuId(entity.getMenuId())
                .menuName(entity.getMenuName())
                .menuPrice(entity.getMenuPrice())
                .build();
    }
}
