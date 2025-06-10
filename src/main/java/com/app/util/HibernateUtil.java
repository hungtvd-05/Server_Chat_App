package com.app.util;

import com.app.model.User;
import com.app.model.UserAccount;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static volatile SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    try {
                        sessionFactory = new Configuration()
                                .configure()
                                .addAnnotatedClass(User.class)
                                .addAnnotatedClass(UserAccount.class)
                                .buildSessionFactory();
                        logger.info("SessionFactory created successfully");
                    } catch (Exception e) {
                        logger.error("Failed to create SessionFactory", e);
                        throw new RuntimeException("Hibernate SessionFactory creation failed", e);
                    }
                }
            }
        }
        return sessionFactory;
    }

    public static void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory != null && !sessionFactory.isClosed()) {
                    sessionFactory.close();
                    logger.info("SessionFactory closed");
                }
            }
        }
    }

    public static void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                logger.warn("Transaction rolled back");
            } catch (Exception e) {
                logger.error("Failed to rollback transaction", e);
            }
        }
    }
}