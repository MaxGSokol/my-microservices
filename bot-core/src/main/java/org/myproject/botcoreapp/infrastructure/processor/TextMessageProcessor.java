package org.myproject.botcoreapp.infrastructure.processor;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class TextMessageProcessor {

    public String dateTimeFormater(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        return formatter.format(localDateTime);
    }

    public String telBookMapFormater(Map<String, String> telBook) {
        StringBuilder stringBuilder = new StringBuilder();
        telBook.entrySet().stream().forEach(entry -> stringBuilder
                .append("Имя - ")
                .append(entry.getKey())
                .append(" ")
                .append("номер - ")
                .append(entry.getValue())
                .append("\n")
        );
        return stringBuilder.toString();
    }
}
