package ru.dto;

import lombok.Data;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import ru.model.CardStatus;

import java.time.LocalDate;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CardResponse {
    Long id;
    String cardNumber;
    String cardholderName;
    LocalDate expirationDate;
    CardStatus status;
    Double balance;
}