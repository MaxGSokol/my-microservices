package org.myproject.botapp.infrastructure.telegram.session.menu;

import lombok.Getter;

@Getter
public enum GeneralBotOptionsMenu {
    START("/start"),
    START_ADMIN("[Меню администратора]"),
    RETURN_TO_MAIN_MENU("[Вернутся в главное меню]"),
    END_SESSION("[Завершить работу бота]");

    private final String buttonText;

    GeneralBotOptionsMenu(String buttonText) {
        this.buttonText = buttonText;
    }
}
