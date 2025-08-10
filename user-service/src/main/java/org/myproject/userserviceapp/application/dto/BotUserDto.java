package org.myproject.userserviceapp.application.dto;

import lombok.Data;
import org.myproject.userserviceapp.domain.enums.UserRole;

@Data
public class BotUserDto {
   private long chatId;
   private String name;
   private String telNum;
   private UserRole role;
   private boolean isRegistered;
}
