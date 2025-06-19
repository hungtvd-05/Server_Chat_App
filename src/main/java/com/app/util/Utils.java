/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.util;

import java.security.SecureRandom;

/**
 *
 * @author ngoan
 */
public class Utils {
    
    public static String generateOTP(int length) {
        String OTP_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(OTP_CHARACTERS.length());
            sb.append(OTP_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
    
}
