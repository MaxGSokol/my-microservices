package org.myproject.botapp.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botapp.application.dto.MessageDto;
import org.myproject.botapp.infrastructure.telegram.TelegramBot;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiveFromCoreService {
    private final TelegramBot telegramBot;

    @KafkaListener(topics = "output_messages")
    public void receiveMessageFromCore(MessageDto messageDto) {
        try {
            telegramBot.sendReceivedMessage(messageDto);
        } catch (RuntimeException e) {
            log.error("Ошибка при получении сообщения" + e);

        }

    }
}
