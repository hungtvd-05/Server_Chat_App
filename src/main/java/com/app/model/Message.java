package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "message_type")
    private int messageType;
    
    @Column(name = "from_user_id")
    private Long fromUserID;
    
    @Column(name = "to_user_id")
    private Long toUserID;
    
    @Column(name = "message_content")
    private String content;
    
    @Column(name = "file_extenssion")
    private String fileExtension;
    
    @Column(name = "blurhash")
    private String blurHash;
    
    @Column(name = "height_blur")
    private int height_blur;
    
    @Column(name = "width_blur")
    private int width_blur;
    
    @Column(name = "sent_at")
    private LocalDateTime time;

    public Message(int messageType, Long fromUserID, Long toUserID, String content, LocalDateTime time) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.content = content;
        this.fileExtension = "";
        this.blurHash = "";
        this.time = time;
    }

    public Message(int messageType, Long fromUserID, Long toUserID, String content, String fileExtension, String blurHash, LocalDateTime time) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.content = content;
        this.fileExtension = fileExtension;
        this.blurHash = blurHash;
        this.time = time;
    }
    
    
}
