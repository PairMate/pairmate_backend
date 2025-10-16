package pairmate.pay_service.dto;

import lombok.Getter;
import pairmate.pay_service.domain.Category;

import java.util.List;

@Getter
public class CategoryItemsResponse {
    private final String category;
    private final String categoryDisplayName;
    private final List<ItemResponse> items;

    public CategoryItemsResponse(Category category, List<ItemResponse> items) {
        this.category = category.name();
        this.categoryDisplayName = category.getDisplayName();
        this.items = items;
    }
}