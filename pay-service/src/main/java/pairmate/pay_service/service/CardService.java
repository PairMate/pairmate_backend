package pairmate.pay_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ErrorCode;
import pairmate.pay_service.converter.CardConverter;
import pairmate.pay_service.domain.ChildCards;
import pairmate.pay_service.dto.CardDTO;
import pairmate.pay_service.repository.ChildCardsRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {

    private final ChildCardsRepository childCardRepository;
    private final CardConverter cardConverter;

    /**
     * 카드 등록
     */
    @Transactional
    public CardDTO.CardResponseDTO registerCard(Long userId, CardDTO.CardRequestDTO request) {
        if (childCardRepository.existsByCardNum(request.getCardNumber())) {
            throw new CustomException(ErrorCode.CARD_ALREADY_REGISTERED);
        }
        ChildCards card = cardConverter.toEntity(request, userId);
        ChildCards saved = childCardRepository.save(card);
        return cardConverter.toResponse(saved);
    }

    /**
     * 카드 단건 조회
     */
    public CardDTO.CardResponseDTO getCard(Long userId, Long cardId) {
        ChildCards card = childCardRepository.findById(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));
        return cardConverter.toResponse(card);
    }

    /**
     * 유저별 카드 목록 조회
     */
    public List<CardDTO.CardResponseDTO> getCardList(Long userId) {
        List<ChildCards> cards = childCardRepository.findAllByUserId(userId);
        return cardConverter.toList(cards);
    }

    /**
     * 일일 한도 등록
     */
    @Transactional
    public void registerDailyLimit(Long userId, Long cardId, CardDTO.DailyLimitRequestDTO request) {
        if (request.getDailyLimit() == null || request.getDailyLimit() < 0) {
            throw new CustomException(ErrorCode.INVALID_CARD_INFO);
        }

        ChildCards card = childCardRepository.findByCardId(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));

        if (!card.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.CARD_UNAUTHORIZED);
        }
        card.updateDayLimit(request.getDailyLimit());
    }

    /**
     *  카드 잔액 조회
     */
    @Transactional(readOnly = true)
    public CardDTO.CardCashResponseDTO getCardBalance(Long userId, Long cardId) {
        ChildCards card = childCardRepository.findByCardId(cardId)
                .orElseThrow(() -> new CustomException(ErrorCode.CARD_NOT_FOUND));

        // 카드 소유자 검증
        if (!card.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.CARD_UNAUTHORIZED);
        }
        return new CardDTO.CardCashResponseDTO(card);
    }

}