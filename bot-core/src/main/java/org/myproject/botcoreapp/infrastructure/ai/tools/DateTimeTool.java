package org.myproject.botcoreapp.infrastructure.ai.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botcoreapp.application.service.UserAiSessionService;
import org.myproject.botcoreapp.domain.entity.UserAiSession;
import org.myproject.botcoreapp.infrastructure.utils.ChatParamsHolder;
import org.myproject.botcoreapp.infrastructure.utils.CoreScriptUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DateTimeTool {
    private final UserAiSessionService aiSessionService;

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Tool(description =
            "Поставь напоминания. Всегда используй работу метода getCurrentDateTime()" +
                    " user's timezone, provided in ISO-8601 format",
            returnDirect = true
    )
    public String setAlarm(String isoDateTime) {
        try {
            Long chatId = ChatParamsHolder.CHAT_ID.get();

            ZonedDateTime zonedAlarmTime = ZonedDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME)
                    .withZoneSameInstant(LocaleContextHolder.getTimeZone().toZoneId());
            LocalDateTime alarmTime = zonedAlarmTime.toLocalDateTime();

            UserAiSession userAiSession = aiSessionService.getSingleUser(chatId);
            userAiSession.setAlarm(alarmTime);
            aiSessionService.addUpdateUserAiSession(userAiSession);

            DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy z")
                    .withZone(LocaleContextHolder.getTimeZone().toZoneId());

            return CoreScriptUtils.ALARM_SET + zonedAlarmTime.format(displayFormat);
        } catch (DateTimeParseException e) {
            return "Ошибка преобразования формата даты и времени";
        } catch (Exception e) {
            return "Ошибка при установке будильника";
        }
    }

    @Tool(description = "Покажи установленное напоминание будильник", returnDirect = true)
    public String showAlarm() {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        UserAiSession userAiSession = aiSessionService.getSingleUser(chatId);
        LocalDateTime alarm = userAiSession.getAlarm();
        return CoreScriptUtils.ALARM_SET + alarm.format(DateTimeFormatter.ISO_DATE_TIME);
    }


}
