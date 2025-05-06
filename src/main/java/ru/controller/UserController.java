package ru.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.dto.UserRequest;
import ru.dto.UserResponse;
import ru.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Операции для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Создание пользователя", description = "Создает нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь создан")
    @PostMapping
    @ResponseBody
    public UserResponse create(@RequestBody UserRequest request) {
        return userService.create(request);
    }

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя")
    @ApiResponse(responseCode = "200", description = "Информация о пользователе")
    @GetMapping("/{id}")
    @ResponseBody
    public UserResponse get(@PathVariable Long id) {
        return userService.get(id);
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь удален")
    @DeleteMapping("/{id}")
    @ResponseBody
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлен")
    @PutMapping("/{id}")
    @ResponseBody
    public UserResponse update(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.update(id, request);
    }
}