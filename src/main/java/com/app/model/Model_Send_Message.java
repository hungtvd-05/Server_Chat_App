package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Send_Message {
    private long id;
    private int messageType;
    private Long fromUserID;
    private Long toUserID;
    private String encryptedContent;
    private String signature;
    private String encryptedAESKey;
    private String pubkeyDSAFromUser;
    private String fileExtension;
    private String blurHash;
    private int height_blur;
    private int width_blur;
    private String time;
} 
