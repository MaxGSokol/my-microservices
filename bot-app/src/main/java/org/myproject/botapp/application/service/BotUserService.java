package org.myproject.botapp.application.service;

import lombok.RequiredArgsConstructor;
import org.myproject.botapp.application.dto.UserServiceDto;
import org.myproject.botapp.infrastructure.feign.BotFeign;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BotUserService {
    private final BotFeign botFeign;

    public Map<Long, UserServiceDto> getAllUsers() {
        return getMap(botFeign.getAll());
    }

    public Map<Long, UserServiceDto> getRegisteredUsers() {
        return getMap(botFeign.getRegisteredUsers());
    }

    public Map<Long, UserServiceDto> getAdmin() {
        return getMap(botFeign.getAdmin());
    }

    public UserServiceDto getUser(long chatId) {
        return botFeign.getUserById(chatId);
    }

    public UserServiceDto getUnregisteredUser() {
        return botFeign.getUnregisteredUser();
    }

    public void create(UserServiceDto userServiceDto) {
        botFeign.create(userServiceDto);
    }

    public void update(UserServiceDto userServiceDto) {
        botFeign.update(userServiceDto);
    }

    public void delete(long chatId) {
        botFeign.deleteUser(chatId);
    }

    private Map<Long, UserServiceDto> getMap(List<UserServiceDto> dtoList) {
        Map<Long, UserServiceDto> dtoMap = new ConcurrentHashMap<>();
        for (UserServiceDto dto : dtoList) {
            dtoMap.put(dto.getChatId(), dto);
        }
        return dtoMap;
    }
}
