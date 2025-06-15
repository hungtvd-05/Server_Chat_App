package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestUserAccount {
    private long userId;
    private String userName;
    private String fullName;
    private String mail;
    private String phone;
    private String gender;
    private boolean status;
    private String image;
    private String pubkeyDSA; 
    private String pubkeyRSA;
}
