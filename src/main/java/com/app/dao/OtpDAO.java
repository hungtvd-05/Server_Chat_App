/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.app.dao;

import com.app.model.Otp;
import com.app.util.HibernateUtil;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author ngoan
 */
public class OtpDAO {

    public CompletableFuture<Boolean> saveOtp(String username, String otpCode, LocalDateTime expiredAt) {
        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                session.createQuery("DELETE FROM Otp o WHERE o.username = :username")
                       .setParameter("username", username)
                       .executeUpdate();

                Otp otp = new Otp();
                otp.setUsername(username);
                otp.setOtpCode(otpCode);
                otp.setExpiredAt(expiredAt);

                session.persist(otp);
                transaction.commit();
                return true;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    HibernateUtil.rollbackTransaction(transaction);
                }
                e.printStackTrace(); 
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> verifyOtp(String username, String inputOtp) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    String hql = "FROM Otp o WHERE o.username = :username AND o.otpCode = :otpCode AND o.expiredAt > :now";
                    Query<Otp> query = session.createQuery(hql, Otp.class);
                    query.setParameter("username", username);
                    query.setParameter("otpCode", inputOtp);
                    query.setParameter("now", LocalDateTime.now());

                    Otp otp = query.uniqueResult();
                    transaction.commit();

                    return otp != null;
                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    throw new RuntimeException("Lỗi khi xác minh OTP: " + e.getMessage(), e);
                }
            }
        });
    }

    public CompletableFuture<Void> deleteOtpByUsername(String username) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.createQuery("DELETE FROM Otp o WHERE o.username = :username")
                       .setParameter("username", username)
                       .executeUpdate();
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    HibernateUtil.rollbackTransaction(transaction);
                }
                throw new RuntimeException("Lỗi khi xoá OTP: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<Otp> getOtpByUsername(String username) {
    return CompletableFuture.supplyAsync(() -> {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                String hql = "FROM Otp o WHERE o.username = :username";
                Query<Otp> query = session.createQuery(hql, Otp.class);
                query.setParameter("username", username);
                query.setMaxResults(1);  // Chỉ lấy 1 bản ghi gần nhất
                Otp otp = query.uniqueResult();
                transaction.commit();
                return otp;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException("Lỗi khi lấy OTP: " + e.getMessage(), e);
            }
        }
    });
}
}