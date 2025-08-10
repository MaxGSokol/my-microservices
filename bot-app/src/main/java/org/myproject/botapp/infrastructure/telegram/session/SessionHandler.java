package org.myproject.botapp.infrastructure.telegram.session;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface SessionHandler {

    boolean canHandle(Message message);

    void handlerExecute(TelegramClient telegramClient, Message message);
}
