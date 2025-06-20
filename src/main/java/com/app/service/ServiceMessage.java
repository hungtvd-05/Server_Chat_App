package com.app.service;

import com.app.dao.MessageDAO;
import com.app.model.History;
import com.app.model.Message;
import com.app.model.Model_Send_Message;
import java.time.LocalDateTime;
import java.util.List;

public class ServiceMessage {
    private MessageDAO messageDAO = new MessageDAO();
    
    public void addMessage(Model_Send_Message ms) {
//        System.out.println("1111111");
        messageDAO.addMessage(new Message(
                ms.getMessageType(), ms.getFromUserID(), ms.getToUserID(), ms.getContent(), LocalDateTime.now()
        ));
    }
    
    public List<Model_Send_Message> getHistoryMessage(History h) {
        return messageDAO.getHistoryMessage(h.getFromUserID(), h.getToUserID()).join();
    }
}
