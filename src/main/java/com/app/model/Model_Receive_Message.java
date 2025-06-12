package com.app.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Receive_Message {
    private int messageType;
    private Long fromUserID;
    private String text;
    Model_Receive_Image dataImage;
    private String time;
} 
