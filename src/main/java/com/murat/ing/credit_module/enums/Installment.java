package com.murat.ing.credit_module.enums;

import java.util.Arrays;

public enum Installment {
    SIX(6), NINE(9), TWELVE(12), TWENTY_FOUR(24);

    private final int value;

    Installment(int value) {
        this.value = value;
    }

    public static boolean isValid(int value) {
        return Arrays.stream(values()).anyMatch(installment -> installment.value == value);
    }

    public int getValue() {
        return value;
    }
}
