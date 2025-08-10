package org.myproject.botcoreapp.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.myproject.botcoreapp.domain.entity.SessionHistoryContext;
import org.myproject.botcoreapp.domain.enums.MessageRole;
import org.myproject.botcoreapp.domain.repository.SessionHistoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionHistoryService {
    private final SessionHistoryRepository historyRepository;


    public List<Message> getContext(long chatId) {
        return historyRepository.findLastTenRecords(chatId).stream()
                .map(historyRecord -> {
                    return switch (historyRecord.getRole()) {
                        case USER -> new UserMessage(historyRecord.getContent());
                        case ASSISTANT -> new AssistantMessage(historyRecord.getContent());
                        case SYSTEM -> new SystemMessage(historyRecord.getContent());
                    };
                })
                .collect(Collectors.toList());
    }

    public void saveUserMessage(long chatId, String content) {
        saveHistoryRecord(chatId, MessageRole.USER, content);
    }

    public void saveAssistantMessage(long chatId, String content) {
        saveHistoryRecord(chatId, MessageRole.ASSISTANT, content);
    }

    private void saveHistoryRecord(long chatId, MessageRole role, String content) {
        SessionHistoryContext sessionHistoryContext = new SessionHistoryContext();
        sessionHistoryContext.setChatId(chatId);
        sessionHistoryContext.setRole(role);
        sessionHistoryContext.setContent(content);
        sessionHistoryContext.setTimeStamp(LocalDateTime.now());
        historyRepository.save(sessionHistoryContext);
    }

    @Transactional
    public void clearHistory(LocalDateTime cutTime) {
        historyRepository.deleteByTimeStampBefore(cutTime);
    }
}
