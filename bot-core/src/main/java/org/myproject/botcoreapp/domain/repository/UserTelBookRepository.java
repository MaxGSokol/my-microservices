package org.myproject.botcoreapp.domain.repository;

import org.myproject.botcoreapp.domain.entity.UserTelephoneBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Map;

public interface UserTelBookRepository extends JpaRepository<UserTelephoneBook, Long> {

    @Query(value = "SELECT tel_book FROM user_tel_book WHERE chat_id = ?", nativeQuery = true)
    Map<String, String> findTelBookByChatId(long chatId);

    UserTelephoneBook findUserTelephoneBooksByChatId(long chatId);
}
