package org.myproject.botapp.infrastructure.telegram;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.myproject.botapp.application.dto.MessageDto;
import org.myproject.botapp.infrastructure.telegram.config.TelegramBotConfig;
import org.myproject.botapp.infrastructure.telegram.session.SessionHandler;
import org.myproject.botapp.infrastructure.telegram.session.SessionHandlerFactory;
import org.myproject.botapp.infrastructure.telegram.session.menu.GeneralBotOptionsMenu;
import org.myproject.botapp.infrastructure.telegram.utils.BotKeyboards;
import org.myproject.botapp.infrastructure.telegram.utils.BotMessages;
import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    private final TelegramBotConfig config;
    private final TelegramClient telegramClient;
    private final SessionHandlerFactory factory;
    private final BotMessages botMessages;
    private final BotKeyboards botKeyboards;

    public TelegramBot(TelegramBotConfig config, SessionHandlerFactory factory, BotMessages botMessages, BotKeyboards botKeyboards) {
        this.config = config;
        this.factory = factory;
        this.botMessages = botMessages;
        this.botKeyboards = botKeyboards;
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectionTimeout(), TimeUnit.SECONDS)
                .readTimeout(config.getRequestTimeout(), TimeUnit.SECONDS);
        this.telegramClient = new OkHttpTelegramClient(httpClientBuilder.build(), getBotToken());
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() && (!update.getMessage().hasVoice() || !update.getMessage().hasText())) {
            return;
        }
        Message message = update.getMessage();
        try {
            SessionHandler handler = factory.getHandler(message);
            handler.handlerExecute(telegramClient, message);
        } catch (Exception e) {
            exceptionResponse(telegramClient, message);
        }
    }

    public void sendReceivedMessage(MessageDto messageDto) {
        botMessages.textMessage(telegramClient, messageDto.getChatId(), messageDto.getTextMessage());
    }

    private void exceptionResponse(TelegramClient telegramClient, Message message) {
        botMessages.textMessage(
                telegramClient,
                message.getChatId(),
                BotScriptUtil.ERROR_SESSION_MESSAGE
                        + BotScriptUtil.END_SESSION
                        + GeneralBotOptionsMenu.END_SESSION.getButtonText()
                        + BotScriptUtil.TO_MAIN_MENU
                        + GeneralBotOptionsMenu.RETURN_TO_MAIN_MENU.getButtonText()
        );
        botKeyboards.errorMenuKeyboard(telegramClient, message);
    }
}
