package ru.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.dto.CardRequest;
import ru.dto.CardResponse;
import ru.dto.CardSearchCriteria;
import ru.service.CardService;
import ru.service.UserService;


@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<CardResponse> searchCards(
            @AuthenticationPrincipal UserDetails userDetails,
            CardSearchCriteria criteria,
            @PageableDefault(size = 10, sort = "expirationDate") Pageable pageable) {

        Long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return cardService.searchCards(userId, criteria, pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CardResponse createCard(@RequestBody CardRequest request) {
        return cardService.createCard(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    public CardResponse blockCard(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public CardResponse activateCard(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public CardResponse getCardById(@PathVariable Long id) {
        return cardService.getCardById(id);
    }
}