package org.myproject.botapp.infrastructure.telegram.session.impl;

import lombok.RequiredArgsConstructor;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.application.service.BotUserService;
import org.myproject.botapp.infrastructure.telegram.session.SessionHandler;
import org.myproject.botapp.infrastructure.telegram.session.menu.GeneralBotOptionsMenu;
import org.myproject.botapp.infrastructure.telegram.session.menu.UserSessionMenuOptions;
import org.myproject.botapp.infrastructure.telegram.utils.BotKeyboards;
import org.myproject.botapp.infrastructure.telegram.utils.BotMessages;
import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Order(10)
@Component
@RequiredArgsConstructor
public class GreetingSessionHandler implements SessionHandler {
    private final BotMessages botMessages;
    private final BotKeyboards botKeyboards;
    private final BotUserService botUserService;

    @Override
    public boolean canHandle(Message message) {
        return message.hasText() && message.getText().equals(GeneralBotOptionsMenu.START.getButtonText())
                || message.hasText() && message.getText().equals(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())
                || message.hasText() && message.getText().equals(GeneralBotOptionsMenu.END_SESSION.getButtonText());
    }

    @Override
    public void handlerExecute(TelegramClient telegramClient, Message message) {
        if (message.getText().equals(GeneralBotOptionsMenu.END_SESSION.getButtonText())) {
            botKeyboards.removeKeyboard(telegramClient, message, BotScriptUtil.GOOD_BY);
            return;
        }
        UserServiceDto userServiceDto = botUserService.getUser(message.getChatId());

        if (userServiceDto == null) {
            toRegistration(telegramClient, message);
        } else if (userServiceDto.isRegistered()) {
            toMainMenu(telegramClient, message, userServiceDto);
        } else {
            botMessages.textMessage(telegramClient, message.getChatId(), BotScriptUtil.REGISTRATION_IN_PROGRESS);
        }
    }

    private void toRegistration(TelegramClient telegramClient, Message message) {
        botMessages.textMessage(
                telegramClient,
                message.getChatId(),
                BotScriptUtil.BOT_GREETING
                        + BotScriptUtil.WELCOME_NEW_USER
                        + BotScriptUtil.FOLLOW_THE_INSTRUCTIONS
        );
        botKeyboards.keyboardWithChooseActionMessage(
                telegramClient,
                message,
                unRegisteredUsersKeyboard()
        );
    }

    private void toMainMenu(TelegramClient telegramClient, Message message, UserServiceDto userServiceDto) {
        botMessages.textMessage(
                telegramClient,
                message.getChatId(),
                BotScriptUtil.BOT_GREETING + BotScriptUtil.WELCOME_REGISTERED_USER + userServiceDto.getName()
        );
        botKeyboards.mainMenuKeyBoard(telegramClient, message, userServiceDto.getRole());

    }

    private List<KeyboardRow> unRegisteredUsersKeyboard() {
        return List.of(
                new KeyboardRow(new KeyboardButton(UserSessionMenuOptions.START_REGISTRATION.getButtonText())),
                new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.END_SESSION.getButtonText()))
        );
    }

}
