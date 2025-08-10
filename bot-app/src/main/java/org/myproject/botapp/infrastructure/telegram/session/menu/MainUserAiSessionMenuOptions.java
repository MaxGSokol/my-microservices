package org.myproject.botapp.infrastructure.telegram.session.menu;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MainUserAiSessionMenuOptions {
    CHAT_WITH_AI("[Перейти к общению с AI]"),
    END_SESSION("[Завершить общение с ботом]");

    private final String buttonText;


    MainUserAiSessionMenuOptions(String buttonText) {
        this.buttonText = buttonText;
    }

    public static MainUserAiSessionMenuOptions fromText(String text) {
        return Arrays.stream(values())
                .filter(options -> options.getButtonText().equals(text))
                .findFirst()
                .orElse(null);
    }
}
