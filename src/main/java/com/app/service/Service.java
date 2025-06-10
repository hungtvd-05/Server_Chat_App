package com.app.service;

import com.app.model.Model_Client;
import com.app.model.Model_Login;
import com.app.model.Model_Message;
import com.app.model.Model_Register;
import com.app.model.TestUserAccount;
import com.app.model.UserAccount;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

public class Service {
    private static Service instance;
    private SocketIOServer server;
    private ServiceUser serviceUser;
    private List<Model_Client> listClient;
    private JTextArea textArea;
    private final int PORT_NUMBER = 9999;
    public static Service getInstance(JTextArea textArea) {
        if (instance == null) {
            instance = new Service(textArea);
        }
        return instance;
    }
    private Service(JTextArea textArea) {
        this.textArea = textArea;
        serviceUser = new ServiceUser();
        listClient = new ArrayList<>();
        serviceUser.updateAllStatus();
        
    }
    public void startServer() {
        Configuration config = new Configuration();
        config.setPort(PORT_NUMBER);
        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient sioc) {
                textArea.append("One client connection\n");
            }
        });
        server.addEventListener("register", Model_Register.class, new DataListener<Model_Register>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Register t, AckRequest ar) throws Exception {
                Model_Message message = serviceUser.register(t);
                ar.sendAckData(message.isAction(), message.getMessage(), message.getData());
                if (message.isAction()) {
                    textArea.append("User register: " + t.getUserName() + ", Password: " + t.getPassword() + ", Fullname: " + t.getFullName() + ", Mail: " + t.getMail() + ", Phone: " + t.getPhone() + "\n");
                    TestUserAccount medata = (TestUserAccount) message.getData();
                    server.getBroadcastOperations().sendEvent("list_user", medata);
                    addClient(sioc, medata);
                    userConnect(medata.getUserId());
                }
            }
            
        });
        server.addEventListener("login", Model_Login.class, new DataListener<Model_Login>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Login t, AckRequest ar) throws Exception {
                System.out.println(t);
                TestUserAccount login = serviceUser.login(t);
                if (login != null) {
                    textArea.append("Mot may da dang nhap\n");
                    ar.sendAckData(true, login);
                    addClient(sioc, login);
                    userConnect(login.getUserId());
                } else {
                    textArea.append("Mot may da dang nhap that bai\n");
                    ar.sendAckData(false);
                }
            }
        });
        server.addEventListener("list_user", Long.class, new DataListener<Long>() {
            @Override
            public void onData(SocketIOClient sioc, Long userId, AckRequest ar) throws Exception {
                try {
                    List<TestUserAccount> list = serviceUser.getUser(userId);
                    sioc.sendEvent("list_user", list.toArray());
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            
        });
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient sioc) {
                textArea.append("Mot may da dang xuat\n");
                long userID = removeClient(sioc);
                if (userID != 0) {
                    //  removed
                    userDisconnect(userID);
                }
            }
        });
        server.start();
        textArea.append("Server has start on port: " + PORT_NUMBER + "\n");
    }
    
    private void userConnect(long userID) {
        serviceUser.updateStatus(true, userID);
        server.getBroadcastOperations().sendEvent("user_status", Long.valueOf(userID), true);
    }

    private void userDisconnect(long userID) {
        serviceUser.updateStatus(false, userID);
        server.getBroadcastOperations().sendEvent("user_status", Long.valueOf(userID), false);
    }
    
    private void addClient(SocketIOClient client, TestUserAccount user) {
        listClient.add(new Model_Client(client, user));
    }
    
    public long removeClient(SocketIOClient client) {
        for (Model_Client d : listClient) {
            if (d.getClient() == client) {
                listClient.remove(d);
                return d.getUser().getUserId();
            }
        }
        return 0;
    }    
    
    
}
