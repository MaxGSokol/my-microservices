package org.myproject.botcoreapp.domain.repository;

import org.myproject.botcoreapp.domain.entity.SessionHistoryContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SessionHistoryRepository extends JpaRepository<SessionHistoryContext, Long> {

    @Query(value = "SELECT * FROM session_history_context " +
            "WHERE chat_id = ? " +
            "ORDER BY time_stamp DESC " +
            "LIMIT 10",
            nativeQuery = true)
    List<SessionHistoryContext> findLastTenRecords(long chatId);

    void deleteByTimeStampBefore(LocalDateTime timeStampBefore);
}


