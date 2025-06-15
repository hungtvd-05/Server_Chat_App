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
    
    @Column(name = "public_key_dsa_from_user", columnDefinition = "TEXT")
    private String publicKeyDSAFromUser;
    
    @Column(name = "to_user_id")
    private Long toUserID;
    
    @Column(name = "encrypted_content", columnDefinition = "TEXT")
    private String encryptedContent;
    
    @Column(name = "signature", columnDefinition = "TEXT")
    private String signature;
    
    @Column(name = "encrypted_aes_key", columnDefinition = "TEXT")
    private String encryptedAESKey;
    
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

    public Message(int messageType, Long fromUserID, Long toUserID, String encryptedContent, String signature, String encryptedAESKey, LocalDateTime time) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.encryptedContent = encryptedContent;
        this.signature = signature;
        this.encryptedAESKey = encryptedAESKey;
        this.fileExtension = "";
        this.blurHash = "";
        this.time = time;
    }
    
    public Message(int messageType, Long fromUserID, Long toUserID, String encryptedContent, String signature, String encryptedAESKey, String fileExtension, String blurHash, LocalDateTime time) {
        this.messageType = messageType;
        this.fromUserID = fromUserID;
        this.toUserID = toUserID;
        this.encryptedContent = encryptedContent;
        this.signature = signature;
        this.encryptedAESKey = encryptedAESKey;
        this.fileExtension = fileExtension;
        this.blurHash = blurHash;
        this.time = time;
    }
    
    
}
