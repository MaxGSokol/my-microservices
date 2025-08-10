package org.myproject.botcoreapp.infrastructure.ai.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botcoreapp.application.service.UserAiSessionService;
import org.myproject.botcoreapp.infrastructure.utils.ChatParamsHolder;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteTool {
    private final UserAiSessionService sessionService;


    @Tool(description = "Покажи заметки")
    public String showNote() {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        return sessionService.getNote(chatId);
    }

    @Tool(description = "Добавь в заметки", returnDirect = true)
    public String updateNote(String note) {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        return sessionService.addNote(chatId, note);
    }

    @Tool(description = "Очисти удали заметки", returnDirect = true)
    public String deleteNote() {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        return sessionService.clearNote(chatId);
    }

}
