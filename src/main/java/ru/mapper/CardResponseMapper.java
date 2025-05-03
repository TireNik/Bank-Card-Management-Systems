package ru.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.dto.CardResponse;
import ru.encryptionUtil.CardMaskingUtil;
import ru.encryptionUtil.EncryptionUtil;
import ru.model.Card;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardResponseMapper {

    private final EncryptionUtil encryptionUtil;

    public CardResponse toResponse(Card card) {
        String decryptedCardNumber = null;
        String maskedCardNumber = null;

        try {
            decryptedCardNumber = encryptionUtil.decrypt(card.getCardNumber());
            maskedCardNumber = CardMaskingUtil.mask(decryptedCardNumber);
        } catch (Exception e) {
            log.error("Ошибка при расшифровке или маскировании номера карты", e);
            maskedCardNumber = "**** **** **** ????";
        }

        return CardResponse.builder()
                .id(card.getId())
                .ownerName(card.getOwner().getFirstName())
                .maskedCardNumber(maskedCardNumber)
                .expirationDate(card.getExpirationDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }
}