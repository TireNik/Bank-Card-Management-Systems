package ru.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.model.CardStatus;

import java.time.LocalDate;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardResponse {
    Long id;
    String ownerName;
    String maskedCardNumber;
    LocalDate expirationDate;
    CardStatus status;
    Double balance;
}