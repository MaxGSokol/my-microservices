package org.myproject.botcoreapp.application.dto;

import lombok.Data;

@Data
public class MessageDto {
    private long chatId;
    private String name;
    private String telNum;
    private byte[] audioData;
    private String textMessage;
}
