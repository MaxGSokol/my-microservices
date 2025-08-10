package org.myproject.botapp.infrastructure.telegram.session.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botapp.application.dto.MessageDto;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.application.service.BotUserService;
import org.myproject.botapp.application.service.SendToCoreService;
import org.myproject.botapp.exception.UserNotFoundException;
import org.myproject.botapp.infrastructure.telegram.session.SessionHandler;
import org.myproject.botapp.infrastructure.telegram.session.menu.GeneralBotOptionsMenu;
import org.myproject.botapp.infrastructure.telegram.session.menu.MainUserAiSessionMenuOptions;
import org.myproject.botapp.infrastructure.telegram.session.menu.UserSessionMenuOptions;
import org.myproject.botapp.infrastructure.telegram.session.state.MainUserSession;
import org.myproject.botapp.infrastructure.telegram.session.state.MainUserSessionState;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Order(3)
@Slf4j
@Component
@RequiredArgsConstructor
public class MainUserSessionHandler implements SessionHandler {
    private final BotUserService botUserService;
    private final SendToCoreService sendToCoreService;
    private final BotKeyboards botKeyboards;
    private final BotMessages botMessages;
    private final Map<Long, MainUserSession> sessions = new ConcurrentHashMap<>();

    @Override
    public boolean canHandle(Message message) {
        return (
                message.hasText()
                        && message.getText().equals(UserSessionMenuOptions.START_USER_SESSION.getButtonText())
                        && botUserService.getUser(message.getChatId()).isRegistered()
        )
                || sessions.containsKey(message.getChatId());
    }

    @Override
    public void handlerExecute(TelegramClient telegramClient, Message message) {
        MainUserSession userSession = sessions.computeIfAbsent(message.getChatId(), K -> new MainUserSession());
        MainUserAiSessionMenuOptions options = MainUserAiSessionMenuOptions.fromText(message.getText());

        if (options == MainUserAiSessionMenuOptions.END_SESSION) {
            botKeyboards.removeKeyboard(telegramClient, message, BotScriptUtil.GOOD_BY);
            endSession(message);
            return;
        }
        if (message.getText().equals(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())) {
            endSession(message);
            botKeyboards.mainMenuKeyBoard(
                    telegramClient,
                    message,
                    botUserService.getUser(message.getChatId()).getRole()
            );
            return;
        }

        if (options != null) {
            setCurrentSessionState(options, userSession);
        }

        switch (userSession.getCurrentState()) {
            case GREETING -> greeting(telegramClient, message, userSession);
            case CHAT -> {
                if (options == null) {
                    messageManager(telegramClient, message);
                }
            }
        }
    }

    private void greeting(TelegramClient telegramClient, Message message, MainUserSession userSession) {
        botMessages.textMessage(telegramClient, message.getChatId(), BotScriptUtil.LETS_START);
        botKeyboards.constantKeyboard(
                telegramClient,
                message, exitKeyboard(),
                BotScriptUtil.END_SESSION + MainUserAiSessionMenuOptions.END_SESSION.getButtonText());
        userSession.setCurrentState(MainUserSessionState.CHAT);

    }

    private void messageManager(TelegramClient telegramClient, Message message) {
        try {
            sendMessageToCore(message);
        } catch (UserNotFoundException e) {
            botMessages.textMessage(telegramClient, message.getChatId(), e.getMessage());
            endSession(message);
        }

    }

    private void sendMessageToCore(Message message) throws UserNotFoundException {
        sendToCoreService.sendMessageToCore(createMessageToSend(message));
    }

    private MessageDto createMessageToSend(Message message) {
        UserServiceDto userServiceDto = botUserService.getUser(message.getChatId());
        if (userServiceDto == null) {
            throw new UserNotFoundException();
        }
        MessageDto messageDto = new MessageDto();
        messageDto.setChatId(userServiceDto.getChatId());
        messageDto.setName(userServiceDto.getName());
        messageDto.setTelNum(userServiceDto.getTelNum());

        if (message.hasText()) {
            messageDto.setTextMessage(message.getText());
        }
        return messageDto;
    }

    private void setCurrentSessionState(MainUserAiSessionMenuOptions options, MainUserSession userSession) {
        if (options == MainUserAiSessionMenuOptions.CHAT_WITH_AI) {
            userSession.setCurrentState(MainUserSessionState.CHAT);
        }
    }

    private void endSession(Message message) {
        sessions.get(message.getChatId()).setCurrentState(MainUserSessionState.GREETING);
        sessions.remove(message.getChatId());
    }

    private List<KeyboardRow> exitKeyboard() {
        return List.of(
                new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())),
                new KeyboardRow(new KeyboardButton(MainUserAiSessionMenuOptions.END_SESSION.getButtonText()))
        );
    }

}
