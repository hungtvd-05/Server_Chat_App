package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Model_Register {

    private String userName;
    private String password;
    private String fullName;
    private String mail;
    private String phone;
    private String pubkeyDSA; 
    private String pubkeyRSA;
    private String otp;

}
