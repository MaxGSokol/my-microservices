package org.myproject.botapp.infrastructure.telegram.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionHandlerFactory {
    private final List<SessionHandler> handlers;

    public SessionHandler getHandler(Message message) {
        return handlers
                .stream()
                .filter(handler -> handler.canHandle(message))
                .findFirst()
                .orElse( null);
    }
}
