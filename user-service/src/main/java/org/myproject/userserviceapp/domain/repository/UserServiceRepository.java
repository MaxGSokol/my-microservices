package org.myproject.userserviceapp.domain.repository;

import org.myproject.userserviceapp.domain.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserServiceRepository extends JpaRepository<BotUser, Long> {

    BotUser findBotUsersByChatId(long chatId);

    @Query(value = "SELECT * FROM bot_users WHERE role = 'ADMIN'", nativeQuery = true)
    List<BotUser> findAllAdmins();

    @Query(value = "SELECT * FROM bot_users WHERE is_registered = false LIMIT 1", nativeQuery = true)
    BotUser findUnRegisteredBotUser();

    @Query(value = "SELECT * FROM bot_users WHERE role = 'USER'", nativeQuery = true)
    List<BotUser> findAllUsers();

    @Query(value = "SELECT * FROM bot_users WHERE role = 'USER' AND is_registered = true", nativeQuery = true)
    List<BotUser> findRegisteredUsers();
}
