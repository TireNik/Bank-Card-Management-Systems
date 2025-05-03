package ru.encryptionUtil;

public class CardMaskingUtil {

    private static final String MASK = "**** **** **** ";

    public static String mask(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            throw new IllegalArgumentException("Недопустимый номер карты");
        }
        return MASK + cardNumber.substring(cardNumber.length() - 4);
    }
}