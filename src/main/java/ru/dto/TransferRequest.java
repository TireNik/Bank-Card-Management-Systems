package ru.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransferRequest {
    Long senderCardId;
    Long receiverCardId;
    Long amount;
}
