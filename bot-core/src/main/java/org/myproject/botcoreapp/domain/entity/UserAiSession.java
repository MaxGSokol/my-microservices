package org.myproject.botcoreapp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_ai_session")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAiSession {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "chat_id")
    private long chatId;
    private LocalDateTime alarm;
    @Column(length = 1000)
    private String note;
}
