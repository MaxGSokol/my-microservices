package org.myproject.botapp.application.dto;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class MessageDto {
    private long chatId;
    private String name;
    private String telNum;
    private byte[] audioData;
    private String textMessage;
}
