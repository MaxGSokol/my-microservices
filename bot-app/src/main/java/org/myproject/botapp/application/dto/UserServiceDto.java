package org.myproject.botapp.application.dto;

import lombok.Data;

@Data
public class UserServiceDto {
   private long chatId;
   private String name;
   private String telNum;
   private UserRole role;
   private boolean isRegistered;
}
