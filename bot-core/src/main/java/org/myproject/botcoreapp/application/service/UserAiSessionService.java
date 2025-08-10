package org.myproject.botcoreapp.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.myproject.botcoreapp.application.dto.MessageDto;
import org.myproject.botcoreapp.domain.entity.UserAiSession;
import org.myproject.botcoreapp.domain.repository.UserAiSessionRepository;
import org.myproject.botcoreapp.infrastructure.processor.TextMessageProcessor;
import org.myproject.botcoreapp.infrastructure.utils.CoreScriptUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserAiSessionService {
    private final UserAiSessionRepository sessionRepository;
    private final TextMessageProcessor textMessageProcessor;

    @Transactional
    public List<MessageDto> getAlarmMessageList(LocalDateTime alarm) {
        List<UserAiSession> userAiSessions = sessionRepository.findUserAiSessionByAlarmBeforeOrEquals(alarm);
        if (userAiSessions.isEmpty()) {
            return Collections.emptyList();
        }
        List<MessageDto> messages = userAiSessions.stream()
                .map(userAiSession -> {
                    MessageDto messageDto = new MessageDto();
                    messageDto.setChatId(userAiSession.getChatId());
                    messageDto.setTextMessage(
                            CoreScriptUtils.ALARM_RESPONSE
                                    + textMessageProcessor.dateTimeFormater(userAiSession.getAlarm())
                    );
                    return messageDto;
                })
                .toList();
        userAiSessions.forEach(userAiSession -> userAiSession.setAlarm(null));
        sessionRepository.saveAll(userAiSessions);
        return messages;
    }

    public String getNote(long chatId) {
        String note = getSingleUser(chatId).getNote();
        return Objects.requireNonNullElse(note, CoreScriptUtils.NOTE_IS_EMPTY);
    }

    public String addNote(long chatId, String note) {
        String newNote;
        UserAiSession userAiSession = getSingleUser(chatId);
        String oldNote = userAiSession.getNote();
        if (oldNote == null) {
            newNote = note;
        } else {
            newNote = oldNote + "\n" + note;
        }
        userAiSession.setNote(newNote);
        addUpdateUserAiSession(userAiSession);
        return CoreScriptUtils.NOTE_IS_SET;
    }

    public String clearNote(long chatId) {
        UserAiSession userAiSession = getSingleUser(chatId);
        userAiSession.setNote(null);
        addUpdateUserAiSession(userAiSession);
        return CoreScriptUtils.NOTE_DELETED;
    }

    public UserAiSession getSingleUser(long chatId) {
        UserAiSession userAiSession = sessionRepository.findByChatId(chatId);
        if (userAiSession == null) {
            userAiSession = new UserAiSession();
            userAiSession.setChatId(chatId);
            sessionRepository.save(userAiSession);
        }
        return userAiSession;
    }

    public void addUpdateUserAiSession(UserAiSession userAiSession) {
        sessionRepository.save(userAiSession);
    }
}
