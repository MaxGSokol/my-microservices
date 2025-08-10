package org.myproject.botapp.infrastructure.telegram.session.menu;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserSessionMenuOptions {
    START_REGISTRATION("[Начать регистрацию]"),
    START_USER_SESSION("[Приступить к работе]"),
    WITHOUT_TEL_NUM("[Без номера телефона]");

    private final String buttonText;

    UserSessionMenuOptions(String buttonText) {
        this.buttonText = buttonText;
    }

    public static UserSessionMenuOptions fromText(String text) {
        return Arrays.stream(values())
                .filter(option -> option.buttonText.equals(text))
                .findFirst()
                .orElse(null);
    }
}
