/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author LENOVO
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Column(name = "fullname")
    private String fullName;
    
    @Id
    @Column(name = "username")
    private String username;
    
    @Column(name = "password")
    private String password;
    
    @Column(name = "mail")
    private String mail;
    
    @Column(name = "phone")
    private String phone;
    
}
