package pairmate.pay_service.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    MEAL("도시락 및 식사류"),
    PROCESSED("가공식품/유제품/음료"),
    CHILLED("냉장(냉동)식품/간편식");

    private final String displayName;
}