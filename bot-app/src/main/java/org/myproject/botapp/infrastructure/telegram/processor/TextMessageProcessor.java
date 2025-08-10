package org.myproject.botapp.infrastructure.telegram.processor;

import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TextMessageProcessor {


    public List<String> fromMapDto(Map<Long, UserServiceDto> userServiceDtoMap) {
        List<String> messages = new ArrayList<>();
        for (Map.Entry<Long, UserServiceDto> entry : userServiceDtoMap.entrySet()) {
            UserServiceDto value = entry.getValue();
            messages.add(fromSingleDto(value));
        }
        return messages;
    }

    public String fromSingleDto(UserServiceDto userServiceDto) {
        return new StringBuilder()
                .append(userServiceDto.getName()).append("\n")
                .append(userServiceDto.getTelNum()).append("\n")
                .toString();
    }

    public String allDataFromSingleDto(UserServiceDto userServiceDto) {
        return new StringBuilder()
                .append(userServiceDto.getChatId()).append("\n")
                .append(userServiceDto.getName()).append("\n")
                .append(userServiceDto.getTelNum()).append("\n")
                .append(userServiceDto.getRole().name()).append("\n")
                .append(userServiceDto.isRegistered())
                .toString();
    }

    public String getGreetingText(UserServiceDto dto) {
        return BotScriptUtil.GREETING + getUserNameText(dto);
    }

    public String getUserNameText(UserServiceDto dto) {
        return dto.getName() + " \n";
    }

}
