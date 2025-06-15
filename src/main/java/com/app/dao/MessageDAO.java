package com.app.dao;

import com.app.model.Message;
import com.app.model.Model_Send_Message;
import com.app.model.User;
import com.app.util.HibernateUtil;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class MessageDAO {

    private UserDAO userDAO = new UserDAO();

    public CompletableFuture<Void> addMessage(Message ms) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                session.persist(ms);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    HibernateUtil.rollbackTransaction(transaction);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Message> saveandgetMessage(Message ms) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userDAO.findById(ms.getFromUserID()).join();
            if (user == null) {
                return null;
            }
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                ms.setPublicKeyDSAFromUser(user.getUserAccount().getPubkeyDSA());
                transaction = session.beginTransaction();
                session.persist(ms);
                session.flush();
                transaction.commit();
                return session.get(Message.class, ms.getId());
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    HibernateUtil.rollbackTransaction(transaction);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<List<Model_Send_Message>> getHistoryMessage(Long fromUserID, Long toUserID, Long fromMessageID) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    String hql = "FROM Message m "
                            + "WHERE ((m.fromUserID = :fromUserID AND m.toUserID = :toUserID) "
                            + "OR (m.fromUserID = :toUserID AND m.toUserID = :fromUserID)) "
                            + "AND m.id > :fromMessageID";
                    Query<Message> query = session.createQuery(hql, Message.class);
                    query.setParameter("fromUserID", fromUserID);
                    query.setParameter("toUserID", toUserID);
                    query.setParameter("fromMessageID", fromMessageID);
                    List<Message> mss = query.getResultList();
                    List<Model_Send_Message> dtoList = mss.stream()
                            .map(msg -> new Model_Send_Message(
                            msg.getId(),
                            msg.getMessageType(),
                            msg.getFromUserID(),
                            msg.getToUserID(),
                            msg.getEncryptedContent(),
                            msg.getSignature(),
                            msg.getEncryptedAESKey(),
                            msg.getPublicKeyDSAFromUser(),
                            msg.getFileExtension(),
                            msg.getBlurHash(),
                            msg.getHeight_blur(),
                            msg.getWidth_blur(),
                            msg.getTime().toString()
                    ))
                            .collect(Collectors.toList());
                    return dtoList;
                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    throw new RuntimeException("Lỗi khi tìm kiếm TestUserAccount: " + e.getMessage(), e);
                }
            }
        });
    }

    public CompletableFuture<Void> updateBlur(Long id, String blur, int height, int width) {
        return CompletableFuture.runAsync(() -> {
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                String hql = "UPDATE Message m SET m.blurHash = :blur, m.height_blur = :height, m.width_blur = :width WHERE m.id = :id";
                Query<?> query = session.createQuery(hql);
                query.setParameter("blur", blur);
                query.setParameter("id", id);
                query.setParameter("height", height);
                query.setParameter("width", width);
                query.executeUpdate();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    HibernateUtil.rollbackTransaction(transaction);
                }
                throw new RuntimeException(e.getMessage(), e);
            }
        });
    }

    public CompletableFuture<Message> getFile(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    String hql = "FROM Message m WHERE m.id = :id";
                    Query<Message> query = session.createQuery(hql, Message.class);
                    query.setParameter("id", id);
                    query.setMaxResults(1);
                    Message ms = query.uniqueResult();
                    transaction.commit();
                    return ms;
                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    throw new RuntimeException("Lỗi khi tìm kiếm người dùng: " + e.getMessage(), e);
                }
            }
        });
    }

}
