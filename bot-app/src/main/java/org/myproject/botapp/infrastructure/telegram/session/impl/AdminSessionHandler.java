package org.myproject.botapp.infrastructure.telegram.session.impl;

import lombok.RequiredArgsConstructor;
import org.myproject.botapp.application.dto.UserRole;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.application.service.BotUserService;
import org.myproject.botapp.infrastructure.telegram.session.SessionHandler;
import org.myproject.botapp.infrastructure.telegram.session.menu.AdminSessionMenuOptions;
import org.myproject.botapp.infrastructure.telegram.session.menu.GeneralBotOptionsMenu;
import org.myproject.botapp.infrastructure.telegram.session.state.AdminSession;
import org.myproject.botapp.infrastructure.telegram.session.state.AdminSessionState;
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
import java.util.concurrent.ConcurrentLinkedDeque;

@Order(1)
@Component
@RequiredArgsConstructor
public class AdminSessionHandler implements SessionHandler {
    private final BotMessages botMessages;
    private final BotUserService botUserService;
    private final ConcurrentLinkedDeque<String> userListToSend = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<UserServiceDto> registrationList = new ConcurrentLinkedDeque<>();
    private final Map<Long, AdminSession> sessions = new ConcurrentHashMap<>();
    private final BotKeyboards botKeyboards;

    @Override
    public boolean canHandle(Message message) {
        return (
                message.hasText()
                        && message.getText().equals(GeneralBotOptionsMenu.START_ADMIN.getButtonText())
                        && botUserService.getAdmin().containsKey(message.getChatId())
        )
                || sessions.containsKey(message.getChatId());
    }

    @Override
    public void handlerExecute(TelegramClient telegramClient, Message message) {
        AdminSession session = sessions.computeIfAbsent(message.getChatId(), k -> new AdminSession());
        AdminSessionMenuOptions option = AdminSessionMenuOptions.fromText(message.getText());

        if (message.getText().equals(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())) {
            endSession(telegramClient, message);
            botKeyboards.mainMenuKeyBoard(telegramClient, message, UserRole.ADMIN);
            return;
        }
        if (message.getText().equals(GeneralBotOptionsMenu.END_SESSION.getButtonText())) {
            endSession(telegramClient, message);
            botKeyboards.removeKeyboard(telegramClient, message, BotScriptUtil.GOOD_BY);
            return;
        }

        if (option != null) {
            setSessionState(option, session);
        }

        switch (session.getCurrentState()) {
            case INIT -> botKeyboards.keyboardWithChooseActionMessage(
                    telegramClient,
                    message,
                    startMenuKeyboard()
            );
            case REGISTRATION -> {
                botKeyboards.keyboardWithChooseActionMessage(
                        telegramClient,
                        message,
                        registrationMenuKeyboard()
                );
                registrationProcess(option, telegramClient, message);
            }
            case CHOOSE_FATE -> {
                botKeyboards.keyboardWithChooseActionMessage(telegramClient, message, chooseFateMenuKeyboard());
                chooseFate(telegramClient, message, option);
            }
            case SEND_MESSAGES_TO_ADMINS -> {
                botKeyboards.keyboardWithChooseActionMessage(
                        telegramClient,
                        message,
                        sendMessageMenuKeyboard()
                );
                if (option == null) {
                    sendMessageToAdmins(telegramClient, message);
                }
            }
            case SEND_MESSAGE_TO_USERS -> {
                botKeyboards.keyboardWithChooseActionMessage(
                        telegramClient,
                        message,
                        sendMessageMenuKeyboard()
                );
                sendMessageToUsers(telegramClient, message);
            }
        }
    }

    private void registrationProcess(AdminSessionMenuOptions options, TelegramClient telegramClient, Message message) {
        if (options == AdminSessionMenuOptions.LOAD_NEXT_USER) {
            loadUnregisteredUser(telegramClient, message);
        }
    }

    private void setSessionState(AdminSessionMenuOptions options, AdminSession state) {
        if (options == AdminSessionMenuOptions.REGISTRATION_MENU) {
            state.setCurrentState(AdminSessionState.REGISTRATION);
        }
        if (options == AdminSessionMenuOptions.SEND_MESSAGE_TO_ADMINS) {
            state.setCurrentState(AdminSessionState.SEND_MESSAGES_TO_ADMINS);
        }
        if (options == AdminSessionMenuOptions.SEND_MESSAGE_TO_USERS) {
            state.setCurrentState(AdminSessionState.SEND_MESSAGE_TO_USERS);
        }
        if (options == AdminSessionMenuOptions.BACKWARD) {
            state.setCurrentState(AdminSessionState.INIT);
        }
    }

    private void loadUnregisteredUser(TelegramClient telegramClient, Message message) {
        UserServiceDto userServiceDto = botUserService.getUnregisteredUser();
        if (userServiceDto == null) {
            noUserAvailable(telegramClient, message);
            return;
        }
        if (registrationList.isEmpty()) {
            botMessages.dtoMessage(
                    telegramClient,
                    message.getChatId(),
                    userServiceDto,
                    BotScriptUtil.USER_WAITING_FOR_REGISTRATION
            );
            registrationList.add(userServiceDto);
        } else {
            botMessages.dtoMessage(
                    telegramClient,
                    message.getChatId(),
                    registrationList.getFirst(),
                    BotScriptUtil.USER_WAITING_FOR_REGISTRATION);
        }
        botKeyboards.keyboardWithChooseActionMessage(telegramClient, message, chooseFateMenuKeyboard());
        sessions.get(message.getChatId()).setCurrentState(AdminSessionState.CHOOSE_FATE);
    }

    private void chooseFate(TelegramClient telegramClient, Message message, AdminSessionMenuOptions options) {
        if (options == AdminSessionMenuOptions.ACCEPT) {
            UserServiceDto userServiceDto = registrationList.pollFirst();
            if (userServiceDto != null) {
                userServiceDto.setRegistered(true);
                botUserService.update(userServiceDto);
                userListToSend.add(BotScriptUtil.NEW_USER_ADDED + userServiceDto.getName());
                botMessages.textMessage(telegramClient, userServiceDto.getChatId(), BotScriptUtil.REGISTRATION_SUCCESS);
            } else {
                noUserAvailable(telegramClient, message);
            }
        }
        if (options == AdminSessionMenuOptions.REGISTRATION_REFUSE) {
            UserServiceDto userServiceDto = registrationList.pollFirst();
            if (userServiceDto != null) {
                botUserService.delete(userServiceDto.getChatId());
                userListToSend.add(BotScriptUtil.REGISTRATION_DENIED + userServiceDto.getName());
                botMessages.textMessage(telegramClient, userServiceDto.getChatId(), BotScriptUtil.REGISTRATION_DENIED);
            } else {
                noUserAvailable(telegramClient, message);
            }
        }
        sessions.get(message.getChatId()).setCurrentState(AdminSessionState.REGISTRATION);
        botKeyboards.keyboardWithChooseActionMessage(telegramClient, message, registrationMenuKeyboard());
    }

    private void noUserAvailable(TelegramClient telegramClient, Message message) {
        botMessages.textMessage(telegramClient, message.getChatId(), BotScriptUtil.NO_USER_AVAILABLE);
        sessions.get(message.getChatId()).setCurrentState(AdminSessionState.INIT);
    }

    private void sendUserList(
            TelegramClient telegramClient,
            Message message,
            ConcurrentLinkedDeque<String> userListToSend
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String data : userListToSend) {
            stringBuilder.append(data).append("\n");
        }
        botMessages.sendTextToAll(telegramClient, message, botUserService.getAdmin(), stringBuilder.toString());
    }

    private void sendMessageToAdmins(TelegramClient telegramClient, Message message) {
        if (message.hasText()) {
            botMessages.sendTextToAll(telegramClient, message, botUserService.getAdmin(), message.getText());
        }
        if (message.hasVoice()) {
            botMessages.sendVoiceToAll(telegramClient, message, botUserService.getAdmin());
        }
    }

    private void sendMessageToUsers(TelegramClient telegramClient, Message message) {
        if (message.hasVoice()) {
            botMessages.sendTextToAll(telegramClient, message, botUserService.getRegisteredUsers(), message.getText());
        }
        if (message.hasVoice()) {
            botMessages.sendVoiceToAll(telegramClient, message, botUserService.getRegisteredUsers());
        }
    }

    private void endSession(TelegramClient telegramClient, Message message) {
        if (!userListToSend.isEmpty()) {
            sendUserList(telegramClient, message, userListToSend);
        }
        registrationList.clear();
        userListToSend.clear();
        sessions.remove(message.getChatId());
    }

    private List<KeyboardRow> startMenuKeyboard() {
        return List.of(
                new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.REGISTRATION_MENU.getButtonText())),
                new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.SEND_MESSAGE_TO_ADMINS.getButtonText())),
                new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.SEND_MESSAGE_TO_USERS.getButtonText())),
                new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())),
                new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.END_SESSION.getButtonText()))
        );
    }

    private List<KeyboardRow> registrationMenuKeyboard() {
        return List.of(
                (new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.LOAD_NEXT_USER.getButtonText()))),
                (new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.BACKWARD.getButtonText())))
        );
    }

    private List<KeyboardRow> chooseFateMenuKeyboard() {
        return List.of(
                (new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.ACCEPT.getButtonText()))),
                (new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.REGISTRATION_REFUSE.getButtonText())))

        );
    }

    private List<KeyboardRow> sendMessageMenuKeyboard() {
        return List.of(
                new KeyboardRow(new KeyboardButton(AdminSessionMenuOptions.BACKWARD.getButtonText()))
        );
    }

}
