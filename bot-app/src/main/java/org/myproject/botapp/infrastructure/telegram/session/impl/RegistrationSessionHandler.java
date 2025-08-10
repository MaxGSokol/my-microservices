package org.myproject.botapp.infrastructure.telegram.session.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botapp.application.dto.UserRole;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.application.service.BotUserService;
import org.myproject.botapp.exception.WrongInputException;
import org.myproject.botapp.infrastructure.telegram.session.SessionHandler;
import org.myproject.botapp.infrastructure.telegram.session.menu.GeneralBotOptionsMenu;
import org.myproject.botapp.infrastructure.telegram.session.menu.UserSessionMenuOptions;
import org.myproject.botapp.infrastructure.telegram.session.state.RegistrationSession;
import org.myproject.botapp.infrastructure.telegram.session.state.RegistrationSessionFields;
import org.myproject.botapp.infrastructure.telegram.utils.BotKeyboards;
import org.myproject.botapp.infrastructure.telegram.utils.BotMessages;
import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class RegistrationSessionHandler implements SessionHandler {
    @Value("${telegram.admin.password}")
    private String password;
    private final BotUserService botUserService;
    private final BotMessages botMessages;
    private final BotKeyboards botKeyboards;
    private final Map<Long, RegistrationSession> sessions = new ConcurrentHashMap<>();
    private final List<RegistrationSessionFields> fields = Arrays.stream(RegistrationSessionFields.values()).toList();

    @Override
    public boolean canHandle(Message message) {
        return (
                message.hasText()
                        && message.getText().equals(UserSessionMenuOptions.START_REGISTRATION.getButtonText()))
                || message.hasText() && message.getText().equals(password)
                || sessions.containsKey(message.getChatId());
    }

    @Override
    public void handlerExecute(TelegramClient telegramClient, Message message) {
        RegistrationSession state = sessions.computeIfAbsent(
                message.getChatId(),
                k -> new RegistrationSession()
        );
        if (message.getText().equals(GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText())) {
            endSession(message, state);
            botKeyboards.keyboardWithChooseActionMessage(telegramClient, message, startButtonKeyboard());
        }
        if (message.getText().equals(GeneralBotOptionsMenu.END_SESSION.getButtonText())) {
            endSession(message, state);
            botKeyboards.removeKeyboard(telegramClient, message, BotScriptUtil.GOOD_BY);
        }

        if (
                message.getText().equals(UserSessionMenuOptions.START_REGISTRATION.getButtonText())
                        || message.getText().equals(password)
        ) {
            beginRegistration(telegramClient, message, state);
        } else {
            processFieldInput(telegramClient, state, message);
        }
    }

    private void beginRegistration(TelegramClient telegramClient, Message message, RegistrationSession state) {
        state.reset();
        if (message.getText().equals(password)) {
            state.setAdmin(true);
            UserServiceDto userServiceDto = botUserService.getUser(message.getChatId());
            if (userServiceDto != null) {
                state.setUserServiceDto(userServiceDto);
                setAdminStatus(state);
                botUserService.update(state.getUserServiceDto());
                adminProceed(telegramClient, message, state);
                endSession(message, state);
            }
        }
        askForNextField(message, telegramClient, state);

    }

    private void processFieldInput(TelegramClient telegramClient, RegistrationSession state, Message message) {
        if (message.getText() == null) {
            botMessages.textMessage(telegramClient, message.getChatId(), BotScriptUtil.WRONG_ACTION);
            return;
        }
        try {
            setFieldValue(state, message);
            if (hasMoreField(state)) {
                askForNextField(message, telegramClient, state);
            } else {
                completeRegistration(telegramClient, message, state);
            }
        } catch (WrongInputException e) {
            askForFieldAgain(telegramClient, message, e);
        }
    }

    private void askForNextField(Message message, TelegramClient telegramClient, RegistrationSession state) {
        int nextIndex = state.getCurrentField() == null
                ? 0
                : fields.indexOf(state.getCurrentField()) + 1;

        RegistrationSessionFields nextField = fields.get(nextIndex);
        state.setCurrentField(nextField);

        String prompt = setFieldPrompt(nextField);
        if (nextField.equals(RegistrationSessionFields.TEL_NUM)) {
            botKeyboards.keyboardWithChooseActionMessage(
                    telegramClient,
                    message,
                    skipTelNumKeyboard()
            );
        }
        botMessages.textMessage(telegramClient, message.getChatId(), prompt);
    }

    private void askForFieldAgain(
            TelegramClient telegramClient,
            Message message,
            WrongInputException wrongInputException
    ) {
        botMessages.textMessage(
                telegramClient,
                message.getChatId(),
                wrongInputException.getMessage() + BotScriptUtil.REPEAT_INPUT
        );
    }

    private boolean hasMoreField(RegistrationSession state) {
        int currentIndex = fields.indexOf(state.getCurrentField());
        return currentIndex < fields.size() - 1;
    }

    private void setFieldValue(RegistrationSession state, Message message) throws WrongInputException {
        switch (state.getCurrentField()) {
            case RegistrationSessionFields.NAME -> state.getUserServiceDto().setName(message.getText());
            case RegistrationSessionFields.TEL_NUM -> {
                if (message.getText().equals(UserSessionMenuOptions.WITHOUT_TEL_NUM.getButtonText())) {
                    state.getUserServiceDto().setTelNum(null);
                } else if (isValidTelNum(message.getText())) {
                    state.getUserServiceDto().setTelNum(message.getText());
                } else {
                    throw new WrongInputException();
                }
            }
        }
    }

    private String setFieldPrompt(RegistrationSessionFields field) {
        return switch (field) {
            case NAME -> "Как вы хотите что бы к вам обращались.";
            case TEL_NUM -> "Введите ваш номер телефона.\n" + BotScriptUtil.TEL_NUM_EXAMPLE;
        };
    }

    private void completeRegistration(TelegramClient telegramClient, Message message, RegistrationSession state) {
        state.getUserServiceDto().setChatId(message.getChatId());
        saveUser(state);
        if (state.isAdmin()) {
            adminProceed(telegramClient, message, state);
        } else {
            userProceed(telegramClient, message, state);
        }
        endSession(message, state);
    }

    private void saveUser(RegistrationSession state) {
        if (state.isAdmin()) {
            setAdminStatus(state);
        } else {
            state.getUserServiceDto().setRole(UserRole.USER);
            state.getUserServiceDto().setRegistered(false);
        }
        botUserService.create(state.getUserServiceDto());
    }

    private void setAdminStatus(RegistrationSession state) {
        state.getUserServiceDto().setRole(UserRole.ADMIN);
        state.getUserServiceDto().setRegistered(true);
    }

    private void adminProceed(TelegramClient telegramClient, Message message, RegistrationSession state) {
        botMessages.textMessage(telegramClient, message.getChatId(), BotScriptUtil.REGISTRATION_SUCCESS);
        botMessages.sendTextToAll(
                telegramClient,
                message,
                botUserService.getAdmin(),
                BotScriptUtil.NEW_ADMIN_ADDED + state.getUserServiceDto().getName()
        );
        botKeyboards.mainMenuKeyBoard(telegramClient, message, UserRole.ADMIN);
    }

    private void userProceed(TelegramClient telegramClient, Message message, RegistrationSession state) {
        botMessages.textMessage(telegramClient, message.getChatId(), BotScriptUtil.REGISTRATION_IN_PROGRESS);
        botMessages.sendTextToAll(
                telegramClient,
                message,
                botUserService.getAdmin(),
                BotScriptUtil.USER_WAITING_FOR_REGISTRATION + state.getUserServiceDto().getName()
        );
    }

    private boolean isValidTelNum(String telNum) {
        String phoneRegex = "^7\\(\\d{3}\\)\\d{3}-?\\d{2}-?\\d{2}$";
        return telNum != null && telNum.matches(phoneRegex);
    }

    private List<KeyboardRow> skipTelNumKeyboard() {
        return List.of(new KeyboardRow(new KeyboardButton(UserSessionMenuOptions.WITHOUT_TEL_NUM.getButtonText())));
    }

    private List<KeyboardRow> startButtonKeyboard() {
        return List.of(new KeyboardRow(new KeyboardButton(GeneralBotOptionsMenu.START.getButtonText())));
    }

    private void endSession(Message message, RegistrationSession state) {
        state.reset();
        sessions.remove(message.getChatId());
    }

}
