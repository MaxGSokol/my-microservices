package org.myproject.botapp.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botapp.application.dto.MessageDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendToCoreService {
    private final KafkaTemplate<String, MessageDto> messageDtoKafkaTemplate;


    public void sendMessageToCore(MessageDto messageDto) {
        messageDtoKafkaTemplate.send("input_messages", messageDto);
    }

}
