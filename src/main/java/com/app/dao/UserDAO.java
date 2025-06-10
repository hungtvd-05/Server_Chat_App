/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.dao;

import com.app.model.User;
import com.app.util.HibernateUtil;
import java.util.concurrent.CompletableFuture;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author LENOVO
 */
public class UserDAO {
    public CompletableFuture<Void> save(User user) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.persist(user);
                transaction.commit();
            } catch (Exception e) {
                HibernateUtil.roolbackTransaction(transaction);
                throw new RuntimeException(e);
            }
        });
    }
}
