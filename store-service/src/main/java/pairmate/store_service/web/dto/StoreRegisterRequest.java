package pairmate.store_service.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class StoreRegisterRequest {
    private Long storeCategoryId;
    private String storeName;
    private String storeContactNumber;
    private String storeMainImageUrl;
    private String storeLocate;
    private String storeType;
    private LocalTime storeOpenTime;
    private LocalTime storeCloseTime;
    private String storeContent;
    private Integer freePeople;
}
