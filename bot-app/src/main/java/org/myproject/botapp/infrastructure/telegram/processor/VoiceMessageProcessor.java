package org.myproject.botapp.infrastructure.telegram.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class VoiceMessageProcessor {


    public byte[] voiceMessageToByte(Voice voice, TelegramClient telegramClient) {
        GetFile getFile = new GetFile(voice.getFileId());
        byte[] data = null;
        try {
            File file = telegramClient.execute(getFile);
            try (InputStream inputStream = telegramClient.downloadFileAsStream(file)) {
                data = inputStream.readAllBytes();
            } catch (IOException e) {
                log.error("Не удалось преобразовать файл!{}", String.valueOf(e));
            }
        } catch (TelegramApiException e) {
            log.error("Не удалось загрузить файл!{}", String.valueOf(e));
        }
        return data;
    }

}
