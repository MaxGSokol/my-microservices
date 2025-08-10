package org.myproject.botcoreapp.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botcoreapp.application.dto.MessageDto;
import org.myproject.botcoreapp.infrastructure.utils.ChatParamsHolder;
import org.myproject.botcoreapp.infrastructure.utils.CoreScriptUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreService {
    private final List<Object> aiTools;
    private final MistralAiChatModel chatModel;
    private final SessionHistoryService historyService;
    private final KafkaTemplate<String, MessageDto> messageDtoKafkaTemplate;

    @KafkaListener(topics = "input_messages")
    public void receiveCommandFromBot(MessageDto messageDto) {
        try {
            ChatParamsHolder.CHAT_ID.set(messageDto.getChatId());
            historyService.saveUserMessage(messageDto.getChatId(), messageDto.getTextMessage());
            String answer = chatAiCommandResponse(
                    messageDto.getChatId(),
                    messageDto.getName(),
                    messageDto.getTextMessage()
            );
            historyService.saveAssistantMessage(messageDto.getChatId(), answer);
            messageDto.setTextMessage(answer);
            sendToBot(messageDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            messageDto.setTextMessage(CoreScriptUtils.SOMETHING_GO_WRONG);
            sendToBot(messageDto);
        } finally {
            ChatParamsHolder.CHAT_ID.remove();
        }
    }

    public void sendToBot(MessageDto messageDto) {
        messageDtoKafkaTemplate.send("output_messages", messageDto);
    }

    private String chatAiCommandResponse(long chatId, String name, String command) {
        String systemMessage = getSystemContext(chatId, name);
        try {
            return ChatClient.create(chatModel)
                    .prompt(systemMessage)
                    .system("Ты общаешься с пользователем имя которого " + name)
                    .user(command)
                    .tools(aiTools.toArray())
                    .call()
                    .content();
        } catch (NonTransientAiException e) {
            return "не удалось обработать запрос" + e;
        }
    }

    private String getSystemContext(long chatId, String name) {
        String context = historyService.getContext(chatId).stream()
                .limit(5)
                .map(Message::getText)
                .collect(Collectors.joining("\n"));

        return """
                Ты общаешься с #[USER_NAME]#.
                Последний контекст:
                #[CONTEXT]#
                
                Правила:
                1. Учитывай контекст, но не упоминай его явно
                2. Отвечай на языке пользователя
                """
                .replace("#[USER_NAME]#", name)
                .replace("#[CONTEXT]#", context);

    }
}
