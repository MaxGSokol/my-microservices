package org.myproject.userserviceapp.application.controller;

import lombok.RequiredArgsConstructor;
import org.myproject.userserviceapp.application.dto.BotUserDto;
import org.myproject.userserviceapp.application.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserServiceController {
    private final UserService userService;

    @GetMapping(value = "/get/all")
    public ResponseEntity<List<BotUserDto>> sendAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping(value = "/get/unregistered")
    public ResponseEntity<BotUserDto> sendUnRegisteredUser() {
        return ResponseEntity.ok(userService.findUnRegisteredUser());
    }
    @GetMapping(value = "/get/registered/users")
    public ResponseEntity<List<BotUserDto>> sendRegisteredUsers() {
        return ResponseEntity.ok(userService.findAllRegisteredUsers());
    }

    @GetMapping(value = "/get/admin")
    public ResponseEntity<List<BotUserDto>> sendAdmins() {
        return ResponseEntity.ok(userService.findAdmin());
    }

    @GetMapping(value = "/get/user/{chatId}")
    public ResponseEntity<BotUserDto> sendUserById(@PathVariable(name = "chatId") long chatId) {
        return ResponseEntity.ok(userService.findUserById(chatId));
    }

    @PostMapping(value = "/post/single")
    public void createNewUser(@RequestBody BotUserDto botUserDto) {
        userService.create(botUserDto);

    }

    @PutMapping(value = "/update")
    public void updateUser(@RequestBody BotUserDto botUserDto) {
        userService.update(botUserDto, botUserDto.getChatId());
    }

    @DeleteMapping(value = "/delete/user/{chatId}")
    public void deleteUserById(@PathVariable(name = "chatId") long chatId) {
        userService.deleteUserById(chatId);
    }
}
