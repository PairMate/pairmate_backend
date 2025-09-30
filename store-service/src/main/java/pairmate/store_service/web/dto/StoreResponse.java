package pairmate.store_service.web.dto;

import lombok.Builder;
import lombok.Getter;
import pairmate.store_service.domain.Stores;

import java.time.LocalTime;

@Getter
@Builder
public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String storeCategoryName;
    private String storeContactNumber;
    private String storeMainImageUrl;
    private String storeLocate;
    private String storeType;
    private LocalTime storeOpenTime;
    private LocalTime storeCloseTime;
    private String storeContent;
    private Integer freePeople;

    public static StoreResponse fromEntity(Stores entity) {
        return StoreResponse.builder()
                .storeId(entity.getStoreId())
                .storeName(entity.getStoreName())
                .storeCategoryName(entity.getStoreCategory().getStoreCategoryName())
                .storeContactNumber(entity.getStoreContactNumber())
                .storeMainImageUrl(entity.getStoreMainImageUrl())
                .storeLocate(entity.getStoreLocate())
                .storeType(entity.getStoreType())
                .storeOpenTime(entity.getStoreOpenTime())
                .storeCloseTime(entity.getStoreCloseTime())
                .storeContent(entity.getStoreContent())
                .freePeople(entity.getFreePeople())
                .build();
    }
}
