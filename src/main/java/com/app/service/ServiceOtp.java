/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.service;

import com.app.dao.OtpDAO;
import com.app.model.Otp;
import java.time.LocalDateTime;

/**
 *
 * @author ngoan
 */
public class ServiceOtp {
   
    private OtpDAO otpDAO;
    
    public ServiceOtp() {
        otpDAO = new OtpDAO();
    }
    
    public boolean saveOtp(String username, String otp, LocalDateTime expiredAt) {
        return otpDAO.saveOtp(username, otp, expiredAt).join();
    }
    
}
