package ru.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dto.CardRequest;
import ru.dto.CardResponse;
import ru.dto.CardSearchCriteria;
import ru.model.CardStatus;

import java.util.List;

public interface CardService {
    CardResponse createCard(CardRequest request);
    void deleteCard(Long cardId);
    CardResponse blockCard(Long cardId);
    CardResponse activateCard(Long cardId);
    List<CardResponse> getMyCards(CardStatus status, int page, int size);
    CardResponse getCardById(Long cardId);
    Page<CardResponse> searchCards(Long userId, CardSearchCriteria criteria, Pageable pageable);
}