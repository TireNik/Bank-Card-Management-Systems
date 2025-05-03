package ru.service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.dto.CardRequest;
import ru.dto.CardResponse;
import ru.dto.CardSearchCriteria;
import ru.encryptionUtil.CardMaskingUtil;
import ru.encryptionUtil.EncryptionUtil;
import ru.exception.AccessDeniedException;
import ru.exception.NotFoundException;
import ru.mapper.CardResponseMapper;
import ru.model.Card;
import ru.model.CardStatus;
import ru.model.Role;
import ru.model.User;
import ru.repository.CardRepository;
import ru.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final CardResponseMapper cardResponseMapper;

    @Override
    @Transactional
    public CardResponse createCard(CardRequest request) {
        User user = getCurrentUser();

        Card card = new Card();
        try {
            card.setCardNumber(encryptionUtil.encrypt(request.getCardNumber()));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании номера карты", e);
        }

        card.setOwner(user);
        card.setExpirationDate(request.getExpirationDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(0.0);

        Card saved = cardRepository.save(card);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        Card card = getCardOrThrow(cardId);
        checkAccess(card);
        cardRepository.delete(card);
    }

    @Override
    @Transactional
    public CardResponse blockCard(Long cardId) {
        Card card = getCardOrThrow(cardId);
        checkAccess(card);
        card.setStatus(CardStatus.BLOCKED);
        return toResponse(cardRepository.save(card));
    }

    @Override
    @Transactional
    public CardResponse activateCard(Long cardId) {
        Card card = getCardOrThrow(cardId);
        checkAccess(card);
        card.setStatus(CardStatus.ACTIVE);
        return toResponse(cardRepository.save(card));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> getMyCards(CardStatus status, int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Card> cards;

        if (status != null) {
            cards = cardRepository.findByStatusAndUserId(status.toString(), user.getId(), pageable);
        } else {
            cards = cardRepository.findByOwnerId(user.getId(), pageable);
        }

        return cards.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardResponse> searchCards(Long userId, CardSearchCriteria criteria, Pageable pageable) {
        Page<Card> page = cardRepository.searchCards(
                userId,
                criteria.getStatus(),
                criteria.getExpirationDateFrom(),
                criteria.getExpirationDateTo(),
                pageable
        );

        return page.map(cardResponseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponse getCardById(Long cardId) {
        Card card = getCardOrThrow(cardId);
        checkAccess(card);
        return toResponse(card);
    }

    private Card getCardOrThrow(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Карта не найдена"));
    }

    private void checkAccess(Card card) {
        User user = getCurrentUser();
        if (!user.getRole().equals(Role.ADMIN) && !card.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Доступ запрещён");
        }
    }

    private CardResponse toResponse(Card card) {
        try {
            String decrypted = encryptionUtil.decrypt(card.getCardNumber());
            return new CardResponse(
                    card.getId(),
                    card.getOwner().getFirstName(),
                    CardMaskingUtil.mask(decrypted),
                    card.getExpirationDate(),
                    card.getStatus(),
                    card.getBalance()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расшифровке номера карты", e);
        }
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}