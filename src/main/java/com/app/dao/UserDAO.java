package com.app.dao;

import com.app.model.TestUserAccount;
import com.app.model.User;
import com.app.util.HibernateUtil;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UserDAO {

    public CompletableFuture<User> save(User user) {
        return CompletableFuture.supplyAsync(() -> {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.persist(user);
                transaction.commit();
                return user; 
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    HibernateUtil.rollbackTransaction(transaction);
                }
                throw new RuntimeException("Lỗi khi lưu người dùng: " + e.getMessage(), e);
            }
        });
    }


    public CompletableFuture<User> findByUsernameOrEmail(String username, String email) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    String hql = "FROM User u WHERE u.username = :username OR u.mail = :mail";
                    Query<User> query = session.createQuery(hql, User.class);
                    query.setParameter("username", username);
                    query.setParameter("mail", email);
                    query.setMaxResults(1); 
                    User user = query.uniqueResult();
                    transaction.commit();
                    return user;
                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    throw new RuntimeException("Lỗi khi tìm kiếm người dùng: " + e.getMessage(), e);
                }
            }
        });
    }
    
//    public CompletableFuture<List<UserAccount>> getActiveUserAccountsExcludingId(Long excludeId) {
//        return CompletableFuture.supplyAsync(() -> {
//            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//                Transaction transaction = session.beginTransaction();
//                try {
//                    String hql = "SELECT ua.userId, ua.username, ua.gender, ua.image " +
//                                 "FROM UserAccount ua " +
//                                 "WHERE ua.status = :status AND ua.userId != :excludeId";
//                    Query<UserAccount> query = session.createQuery(hql, UserAccount.class);
//                    query.setParameter("status", true);
//                    query.setParameter("excludeId", excludeId);
//                    List<UserAccount> userAccounts = query.getResultList();
//                    transaction.commit();
//                    return userAccounts;
//                } catch (Exception e) {
//                    if (transaction != null && transaction.isActive()) {
//                        transaction.rollback();
//                    }
//                    throw new RuntimeException("Lỗi khi tìm kiếm UserAccount: " + e.getMessage(), e);
//                }
//            }
//        });
//    }
    
    public CompletableFuture<List<TestUserAccount>> getActiveUserAccountsExcludingId(Long excludeId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    String hql = "SELECT new com.app.model.TestUserAccount(" +
                                 "ua.userId, ua.username, ua.fullName, ua.mail, ua.phone, ua.gender, ua.status, ua.image) " +
                                 "FROM UserAccount ua " +
                                 "WHERE ua.userId != :excludeId";
                    Query<TestUserAccount> query = session.createQuery(hql, TestUserAccount.class);
//                    query.setParameter("status", true);
                    query.setParameter("excludeId", excludeId);
                    List<TestUserAccount> userAccounts = query.getResultList();
                    transaction.commit();
                    return userAccounts;
                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    throw new RuntimeException("Lỗi khi tìm kiếm TestUserAccount: " + e.getMessage(), e);
                }
            }
        });
    }
    
    public void updateStatus(Long userId, Boolean status) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "UPDATE UserAccount ua SET ua.status = :status WHERE ua.userId = :userId";
            Query<?> query = session.createQuery(hql);
            query.setParameter("status", status);
            query.setParameter("userId", userId);
            int updatedRows = query.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Không tìm thấy tài khoản với userId: " + userId);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                HibernateUtil.rollbackTransaction(transaction);
            }
            throw new RuntimeException("Lỗi khi cập nhật trạng thái: " + e.getMessage(), e);
        }
    }
    
    public void updateAllStatus() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "UPDATE UserAccount ua SET ua.status = :status";
            Query<?> query = session.createQuery(hql);
            query.setParameter("status", false);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                HibernateUtil.rollbackTransaction(transaction);
            }
            throw new RuntimeException("Lỗi khi cập nhật trạng thái: " + e.getMessage(), e);
        }
    }
}
