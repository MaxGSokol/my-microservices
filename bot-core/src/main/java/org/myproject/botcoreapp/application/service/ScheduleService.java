package org.myproject.botcoreapp.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botcoreapp.application.dto.MessageDto;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final UserAiSessionService sessionService;
    private final SessionHistoryService historyService;
    private final CoreService coreService;


    @Scheduled(fixedRate = 10000)
    public void executeAlarm() {
        LocalDateTime now = LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toLocalDateTime();
        List<MessageDto> messages = sessionService.getAlarmMessageList(now);
        if (!messages.isEmpty()) {
            messages.forEach(coreService::sendToBot);
        }
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void oldMessageCleaner() {
        LocalDateTime cutTime = LocalDateTime.now().minusHours(6);
        log.info(cutTime.toString());
        try {
            historyService.clearHistory(cutTime);
        } catch (EmptyResultDataAccessException e) {
            log.error("Данные не удалены{}", String.valueOf(e));
        }
        log.info("Удалены сообщения старше - {}", cutTime);
    }
}


