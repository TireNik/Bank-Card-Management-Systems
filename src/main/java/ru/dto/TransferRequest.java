package ru.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequest {
    @NotNull
    Long senderCardId;

    @NotNull
    Long receiverCardId;

    @NotNull
    @Positive
    Long amount;
}
