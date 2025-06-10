package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "mail", unique = true)
    private String mail;

    @Column(name = "phone")
    private String phone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "status")
    private Boolean status;
    
    @Column(name = "image")
    private String image;

    public UserAccount(User user, String fullName, String username, String mail, String phone, String gender, String image, Boolean status) {
        this.user = user;
        this.fullName = fullName;
        this.username = username;
        this.mail = mail;
        this.phone = phone;
        this.gender = gender;
        this.image = image;
        this.status = status;
    }
    
    
    
    
}
