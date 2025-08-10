package org.myproject.userserviceapp.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.myproject.userserviceapp.domain.enums.UserRole;

@Data
@Entity
@Table(name = "bot_users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BotUser {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "chat_id", columnDefinition = "BIGINT")
    private long chatId;
    private String name;
    @Column(name = "tel_num")
    private String telNum;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_role")
    private UserRole role;
    @Column(name = "is_registered")
    private boolean isRegistered = false;
}
