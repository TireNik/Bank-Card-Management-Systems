package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Card Management", description = "Операции для управления банковскими картами")
public class CardController {

    private final CardService cardService;
    private final UserService userService;

    @Operation(
            summary = "Поиск карт текущего пользователя",
            description = "Доступно только пользователям с ролью USER. Можно фильтровать по статусу и сроку действия."
    )
    @ApiResponse(responseCode = "200", description = "Список найденных карт")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public Page<CardResponse> searchCards(
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject CardSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 10, sort = "expirationDate") Pageable pageable) {

        Long userId = userService.getUserIdByEmail(userDetails.getUsername());
        return cardService.searchCards(userId, criteria, pageable);
    }

    @Operation(
            summary = "Создание новой карты",
            description = "Доступно только администраторам"
    )
    @ApiResponse(responseCode = "201", description = "Карта успешно создана")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CardResponse createCard(@RequestBody CardRequest request) {
        return cardService.createCard(request);
    }

    @Operation(
            summary = "Удаление карты",
            description = "Доступно только администраторам"
    )
    @ApiResponse(responseCode = "204", description = "Карта удалена")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @Operation(
            summary = "Блокировка карты",
            description = "Блокирует карту по ID. Доступно только администраторам."
    )
    @ApiResponse(responseCode = "200", description = "Карта заблокирована")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    public CardResponse blockCard(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    @Operation(
            summary = "Активация карты",
            description = "Активирует карту по ID. Доступно только администраторам."
    )
    @ApiResponse(responseCode = "200", description = "Карта активирована")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/activate")
    public CardResponse activateCard(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    @Operation(
            summary = "Получить карту по ID",
            description = "Доступно только администраторам"
    )
    @ApiResponse(responseCode = "200", description = "Информация о карте")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public CardResponse getCardById(@PathVariable Long id) {
        return cardService.getCardById(id);
    }
}