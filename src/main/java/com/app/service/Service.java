package com.app.service;

import com.app.model.Model_Message;
import com.app.model.Model_Register;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import javax.swing.JTextArea;

public class Service {
    private static Service instance;
    private SocketIOServer server;
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
                Model_Message message = new ServiceUser().register(t);
                ar.sendAckData(message.isAction(), message.getMessage());
                textArea.append("User register: " + t.getUserName() + ", Password: " + t.getPassword() + ", Fullname: " + t.getFullName() + ", Mail: " + t.getMail() + ", Phone: " + t.getPhone() + "\n");
            }
            
        });
        server.start();
        textArea.append("Server has start on port: " + PORT_NUMBER + "\n");
    }
}
