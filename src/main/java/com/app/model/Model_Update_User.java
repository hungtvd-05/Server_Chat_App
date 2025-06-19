/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ngoan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_Update_User {
    private String userName;
    private String fullName;
    private String mail;
    private String phone;
    private String gender;
    private String image;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
   
}
