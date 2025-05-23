package ru.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import ru.model.Card;

import java.time.LocalDate;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {
    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Неверный формат номера карты")
    String cardNumber;

    @NotNull
    LocalDate expirationDate;
}