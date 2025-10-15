package pairmate.store_service.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchResponse {
    private List<StoreResponse> stores;
    private List<MenuResponse> menus;
    private List<String> storeCategories;
}
