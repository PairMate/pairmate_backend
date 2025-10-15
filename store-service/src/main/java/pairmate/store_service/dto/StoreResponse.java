package pairmate.store_service.dto;

import lombok.Builder;
import lombok.Getter;
import pairmate.common_libs.dto.ReviewStatsDto;
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

    private Double averageStarRating;
    private Long reviewCount;

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

    public static StoreResponse from(Stores entity, ReviewStatsDto stats) {
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
                .averageStarRating(stats.getAverageStarRating()) // 별점 정보 추가
                .reviewCount(stats.getReviewCount())
                .build();
    }
}
