package org.myproject.botcoreapp.domain.repository;

import org.myproject.botcoreapp.domain.entity.UserAiSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserAiSessionRepository extends JpaRepository<UserAiSession, Long> {

    @Query(value = "SELECT u FROM UserAiSession u WHERE u.alarm <= :alarm" )
    List<UserAiSession> findUserAiSessionByAlarmBeforeOrEquals(@Param("alarm") LocalDateTime alarm);

    UserAiSession findByChatId(long chatId);
}
