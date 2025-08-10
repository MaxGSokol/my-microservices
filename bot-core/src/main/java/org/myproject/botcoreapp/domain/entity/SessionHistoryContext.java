package org.myproject.botcoreapp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.myproject.botcoreapp.domain.enums.MessageRole;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "session_history_context")
public class SessionHistoryContext {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "chat_id")
    private long chatId;
    @Enumerated(EnumType.STRING)
    private MessageRole role;
    @Column(length = 300)
    private String content;
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;
}
