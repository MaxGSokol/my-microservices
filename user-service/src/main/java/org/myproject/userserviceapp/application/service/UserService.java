package org.myproject.userserviceapp.application.service;

import lombok.RequiredArgsConstructor;
import org.myproject.userserviceapp.application.dto.BotUserDto;
import org.myproject.userserviceapp.application.dto.mapper.BotUserMapper;
import org.myproject.userserviceapp.domain.entity.BotUser;
import org.myproject.userserviceapp.domain.repository.UserServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceRepository repository;
    private final BotUserMapper botUserMapper;

    public List<BotUserDto> findAll() {
        return repository.findAllUsers()
                .stream()
                .map(botUserMapper::toDto)
                .toList();
    }

    public List<BotUserDto> findAdmin() {
        return repository.findAllAdmins()
                .stream()
                .map(botUserMapper::toDto)
                .toList();
    }

    public BotUserDto findUserById(long chatId) {
        return botUserMapper.toDto(repository.findById(chatId).orElse(null));
    }

    public List<BotUserDto> findAllRegisteredUsers() {
        return repository.findRegisteredUsers()
                .stream()
                .map(botUserMapper::toDto)
                .toList();
    }

    public BotUserDto findUnRegisteredUser() {
        return botUserMapper.toDto(repository.findUnRegisteredBotUser());
    }

    public void update(BotUserDto botUserDto, long chatId) {
        BotUser botUser = repository.findBotUsersByChatId(chatId);
        botUser.setName(botUserDto.getName());
        botUser.setTelNum(botUserDto.getTelNum());
        botUser.setRegistered(botUserDto.isRegistered());
        botUser.setRole(botUserDto.getRole());
        repository.save(botUser);
    }

    public void create(BotUserDto botUserDto) {
        BotUser botUser = botUserMapper.toEntity(botUserDto);
        repository.save(botUser);
    }

    public void deleteUserById(long chatId) {
        repository.deleteById(chatId);
    }


}
