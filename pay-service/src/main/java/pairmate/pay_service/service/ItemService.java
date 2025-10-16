package pairmate.pay_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.pay_service.domain.Category;
import pairmate.pay_service.domain.Item;
import pairmate.pay_service.dto.CategoryItemsResponse;
import pairmate.pay_service.dto.ItemResponse;
import pairmate.pay_service.repository.ItemRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 모든 아이템을 카테고리별로 그룹화하여 조회
     */
    @Transactional(readOnly = true)
    public List<CategoryItemsResponse> getItemsGroupedByCategory() {
        List<Item> allItems = itemRepository.findAll();

        // 품목이 없을 때
        if (allItems.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        // 아이템을 카테고리 기준으로 그룹지어서
        Map<Category, List<ItemResponse>> groupedItems = allItems.stream()
                .collect(Collectors.groupingBy(
                        Item::getCategory,
                        Collectors.mapping(ItemResponse::from, Collectors.toList())
                ));

        return Arrays.stream(Category.values())
                .map(category -> new CategoryItemsResponse(category, groupedItems.getOrDefault(category, List.of())))
                .collect(Collectors.toList());
    }
}