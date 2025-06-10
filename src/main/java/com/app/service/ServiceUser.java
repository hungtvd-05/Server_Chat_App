package com.app.service;

import com.app.connection.DatabaseConnection;
import com.app.model.Model_Message;
import com.app.model.Model_Register;
import com.mysql.cj.xdevapi.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUser {

    private final Connection con;

    public Model_Message register(Model_Register data) {
        Model_Message message = new Model_Message();
        try {
            PreparedStatement p = con.prepareStatement(CHECK_USER);
            p.setString(1, data.getUserName());
            p.setString(2, data.getMail());
            ResultSet r = p.executeQuery();
            if (r.first()) {
                message.setAction(false);
                message.setMessage("User Already Exit");
            } else {
                message.setAction(true);
            }
            r.close();
            p.close();
            if (message.isAction()) {
                p = con.prepareStatement(INSERT_USER);
                p.setString(1, data.getUserName());
                p.setString(2, data.getPassword());
                p.setString(3, data.getFullName());
                p.setString(4, data.getMail());
                p.setString(5, data.getPhone());
                p.execute();
                p.close();
            }
        } catch (SQLException e) {
            message.setAction(false);
            message.setMessage("Server Error");
        }
        System.out.println(message.getMessage());
        return message;
    }

    public ServiceUser() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }

    private final String INSERT_USER = "insert into user (UserName, Password, FullName, Mail, Phone) values (?,?,?,?,?)";
    private final String CHECK_USER = "select UserID from user where UserName = ? or Mail = ? limit 1";
}
