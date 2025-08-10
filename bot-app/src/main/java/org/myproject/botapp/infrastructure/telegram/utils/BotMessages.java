package org.myproject.botapp.infrastructure.telegram.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.infrastructure.telegram.processor.TextMessageProcessor;
import org.myproject.botapp.infrastructure.telegram.processor.VoiceMessageProcessor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotMessages {
    private final VoiceMessageProcessor voiceMessageProcessor;
    private final TextMessageProcessor textMessageProcessor;

    public void textMessage(TelegramClient telegramClient, long chatId, String text) {
        SendMessage textMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            telegramClient.execute(textMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения{} ", String.valueOf(e));
        }
    }

    public void voiceMessageFromVoice(TelegramClient telegramClient, long chatId, Voice voice, String text) {
        byte[] audio = voiceMessageProcessor.voiceMessageToByte(voice, telegramClient);
        SendVoice sendVoice = SendVoice.builder()
                .chatId(chatId)
                .voice(new InputFile(new ByteArrayInputStream(audio), "voice_message.ogg"))
                .caption(text)
                .build();
        try {
            telegramClient.execute(sendVoice);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке голосового сообщения{}", String.valueOf(e));
        }
    }

    public void voiceMessageFromByte(TelegramClient telegramClient, long chatId, byte[] audioData) {
        SendVoice sendVoice = SendVoice.builder()
                .chatId(chatId)
                .voice(new InputFile(new ByteArrayInputStream(audioData), "voice_message.ogg"))
                .build();
        try {
            telegramClient.execute(sendVoice);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке голосового из byte[] сообщения{}", String.valueOf(e));
        }
    }

    public void audioMessageFromVoice(TelegramClient telegramClient, long chatId, Voice voice) {
        byte[] audio = voiceMessageProcessor.voiceMessageToByte(voice, telegramClient);
        SendAudio sendAudio = SendAudio.builder()
                .chatId(chatId)
                .audio(new InputFile(new ByteArrayInputStream(audio), "audio_message.mp3"))
                .caption("Голосовое сообщение успешно преобразовано в аудио")
                .build();
        try {
            telegramClient.execute(sendAudio);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке аудио файла{}", String.valueOf(e));
        }
    }

    public void dtoMessage(TelegramClient telegramClient, long chatId, UserServiceDto dto, String text) {
        SendMessage sendDtoMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text + textMessageProcessor.fromSingleDto(dto))
                .build();
        try {
            telegramClient.execute(sendDtoMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке данных пользователя", e);
        }
    }

    public void sendTextToAll(
            TelegramClient telegramClient,
            Message message,
            Map<Long, UserServiceDto> dtoMap,
            String text
    ) {
        if (dtoMap.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, UserServiceDto> entry : dtoMap.entrySet()) {
            long chatId = entry.getKey();
            if (chatId == message.getChatId()) {
                continue;
            }
            UserServiceDto dto = entry.getValue();
            textMessage(
                    telegramClient,
                    chatId,
                    textMessageProcessor.getGreetingText(dto)
                            + text
                            + "\n"
            );
        }
    }

    public void sendVoiceToAll(TelegramClient telegramClient, Message message, Map<Long, UserServiceDto> dtoMap) {
        if (dtoMap.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, UserServiceDto> entry : dtoMap.entrySet()) {
            long chatId = entry.getKey();
            if (chatId == message.getChatId()) {
                continue;
            }
            UserServiceDto dto = entry.getValue();
            voiceMessageFromVoice(
                    telegramClient,
                    chatId,
                    message.getVoice(),
                    textMessageProcessor.getGreetingText(dto)
                            + BotScriptUtil.MESSAGE_FROM_ADMIN
                            + textMessageProcessor.getUserNameText(dto)
            );
        }
    }

}
