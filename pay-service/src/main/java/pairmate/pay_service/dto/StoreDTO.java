package pairmate.pay_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class StoreDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreResponse {
        private Long storeId;
        private String storeName;
        private String category;
        private String storeType;
        private String address;

        private Long userId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuResponse {
        private Long menuId;
        private String menuName;
        private Integer price;
        private Boolean available;

        private Long storeId;
    }
}