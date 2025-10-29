package pairmate.pay_service.converter;

import org.springframework.stereotype.Component;
import pairmate.pay_service.domain.ChildCards;
import pairmate.pay_service.dto.CardDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CardConverter {

    public ChildCards toEntity(CardDTO.CardRequestDTO request, Long userId) {
        return ChildCards.builder()
                .userId(userId)
                .cardNum(request.getCardNumber())
                .cvc(request.getCvc())
                .expireDate(request.getExpireDate())
                .dayLimit(request.getDayLimit())
                .password(request.getPassword())
                .build();
    }

    public CardDTO.CardResponseDTO toResponse(ChildCards entity) {
        return CardDTO.CardResponseDTO.builder()
                .cardId(entity.getCardId())
                .cardNumber(entity.getCardNum())
                .cvc(entity.getCvc())
                .expireDate(entity.getExpireDate())
                .dayLimit(entity.getDayLimit())
                .password(entity.getPassword())
                .userId(entity.getUserId())
                .build();
    }

    public List<CardDTO.CardResponseDTO> toList(List<ChildCards> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
