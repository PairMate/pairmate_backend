package pairmate.pay_service.dto;

import lombok.Getter;
import pairmate.pay_service.domain.Item;

@Getter
public class ItemResponse {
    private final Long id;
    private final String name;
    private final boolean isProhibit;

    private ItemResponse(Long id, String name, boolean isProhibit) {
        this.id = id;
        this.name = name;
        this.isProhibit = isProhibit;
    }

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.isProhibit());
    }
}