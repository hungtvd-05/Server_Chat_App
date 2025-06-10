/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.util;

import com.app.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
/**
 *
 * @author LENOVO
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private HibernateUtil() {
        super();
    }

    private static SessionFactory buildSessionFactory() {
        return new Configuration()
                .configure()
                .addAnnotatedClass(User.class)
                .buildSessionFactory();

    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void close() {
        getSessionFactory().close();
    }

    public static void roolbackTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        try {
            transaction.rollback();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
