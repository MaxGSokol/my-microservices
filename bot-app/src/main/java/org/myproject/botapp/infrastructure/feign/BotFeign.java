package org.myproject.botapp.infrastructure.feign;

import org.myproject.botapp.application.dto.UserServiceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "bot", url = "${feign.url.address}")
public interface BotFeign {

    @GetMapping(value = "/get/all")
    List<UserServiceDto> getAll();

    @GetMapping(value = "/get/admin")
    List<UserServiceDto> getAdmin();

    @GetMapping(value = "/get/registered/users")
    List<UserServiceDto> getRegisteredUsers();

    @GetMapping(value = "/get/user/{chatId}")
    UserServiceDto getUserById(@PathVariable(name = "chatId") long chatId);

    @GetMapping(value = "/get/unregistered")
    UserServiceDto getUnregisteredUser();

    @PostMapping(value = "/post/single")
    void create(@RequestBody UserServiceDto userServiceDto);

    @PutMapping(value = "/update")
    void update(@RequestBody UserServiceDto userServiceDto);

    @DeleteMapping(value = "/delete/user/{chatId}")
    void deleteUser(@PathVariable(name = "chatId") long chatId);
}
