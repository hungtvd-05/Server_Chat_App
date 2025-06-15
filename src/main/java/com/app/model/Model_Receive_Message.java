package com.app.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Receive_Message {
    private Long messageID;
    private int messageType;
    private Long fromUserID;
    private Long toUserID;
    private String encryptedContent;
    private String signature;
    private String encryptedAESKey;
    private String pubkeyDSAFromUser;
    Model_Receive_Image dataImage;
    private String time;
} 
