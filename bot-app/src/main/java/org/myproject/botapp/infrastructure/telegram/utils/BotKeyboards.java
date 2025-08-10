package org.myproject.botapp.infrastructure.telegram.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botapp.application.dto.UserRole;
import org.myproject.botapp.infrastructure.telegram.session.menu.GeneralBotOptionsMenu;
import org.myproject.botapp.infrastructure.telegram.session.menu.UserSessionMenuOptions;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotKeyboards {

    public void mainMenuKeyBoard(TelegramClient telegramClient, Message message, UserRole userRole) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(
                new KeyboardRow(new KeyboardButton(UserSessionMenuOptions.START_USER_SESSION.getButtonText()))
        );
        if (userRole == (UserRole.ADMIN)) {
            keyboardRows.add(new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.START_ADMIN.getButtonText())));
        }
        keyboardRows.add(new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.END_SESSION.getButtonText())));
        keyboardWithChooseActionMessage(telegramClient, message, keyboardRows);
    }

    public void errorMenuKeyboard(TelegramClient telegramClient, Message message) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())));
        keyboardRows.add(new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.END_SESSION.getButtonText())));
        keyboardWithChooseActionMessage(telegramClient, message, keyboardRows);
    }

    public void keyboardWithChooseActionMessage(
            TelegramClient telegramClient,
            Message message,
            List<KeyboardRow> rows
    ) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(rows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(BotScriptUtil.CHOOSE_ACTION)
                .replyMarkup(keyboardMarkup)
                .build();

        sendKeyboard(telegramClient, sendMessage);
    }

    public void constantKeyboard(TelegramClient telegramClient, Message message, List<KeyboardRow> rows, String text) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(rows);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        SendMessage sendMessage = SendMessage.builder()
                .text(text)
                .chatId(message.getChatId())
                .replyMarkup(keyboardMarkup)
                .build();

        sendKeyboard(telegramClient, sendMessage);
    }

    public void removeKeyboard(TelegramClient telegramClient, Message message, String text) {
        ReplyKeyboardRemove keyboardRemove = ReplyKeyboardRemove.builder()
                .removeKeyboard(true)
                .selective(false)
                .build();
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(text)
                .replyMarkup(keyboardRemove)
                .build();

        sendKeyboard(telegramClient, sendMessage);
    }

    private void sendKeyboard(TelegramClient telegramClient, SendMessage sendMessage) {
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Не удалось загрузить клавиатуру{}", String.valueOf(e));
        }
    }
}
