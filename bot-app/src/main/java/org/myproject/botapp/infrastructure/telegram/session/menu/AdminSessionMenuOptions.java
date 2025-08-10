package org.myproject.botapp.infrastructure.telegram.session.menu;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AdminSessionMenuOptions {
    REGISTRATION_MENU("[Перейти в меню регистрации]"),
    LOAD_NEXT_USER("[Загрузить пользователя ожидающего регистрации]"),
    ACCEPT("[Подтвердить регистрацию]"),
    REGISTRATION_REFUSE("[Отказать в регистрации]"),
    SEND_MESSAGE_TO_USERS("[Послать сообщение всем пользователям]"),
    SEND_MESSAGE_TO_ADMINS("[Послать сообщение всем администраторам]"),
    BACKWARD("Вернутся в главное меню администратора");


    private final String buttonText;

    AdminSessionMenuOptions(String buttonText) {
        this.buttonText = buttonText;
    }

    public static AdminSessionMenuOptions fromText(String text) {
        return Arrays.stream(values())
                .filter(option -> option.buttonText.equals(text))
                .findFirst()
                .orElse(null);
    }
}
