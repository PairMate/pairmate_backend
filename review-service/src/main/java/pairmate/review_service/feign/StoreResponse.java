package pairmate.review_service.feign;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String storeContactNumber;
    private String storeMainImageUrl;
    private String storeLocate;
    private String storeType;
    private String storeContent;
    private Integer freePeople;
}