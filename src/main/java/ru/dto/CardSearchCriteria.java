package ru.dto;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.model.CardStatus;

import java.time.LocalDate;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CardSearchCriteria {
    CardStatus status;
    LocalDate expirationDateFrom;
    LocalDate expirationDateTo;
}
