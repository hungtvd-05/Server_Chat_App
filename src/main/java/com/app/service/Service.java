package com.app.service;

import com.app.app.MessageType;
import com.app.model.History;
import com.app.model.Message;
import com.app.model.Model_Client;
import com.app.model.Model_File;
import com.app.model.Model_Login;
import com.app.model.Model_Message;
import com.app.model.Model_Package_Sender;
import com.app.model.Model_Receive_Image;
import com.app.model.Model_Receive_Message;
import com.app.model.Model_Register;
import com.app.model.Model_Reques_File;
import com.app.model.Model_Send_Message;
import com.app.model.TestUserAccount;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.swing.JTextArea;

public class Service {

    private static Service instance;
    private SocketIOServer server;
    private ServiceUser serviceUser;
    private ServiceMessage serviceMessage;
    private ServiceFile serviceFile;
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
        serviceMessage = new ServiceMessage();
        serviceFile = new ServiceFile();
        listClient = new ArrayList<>();
        serviceUser.updateAllStatus();
    }

    public void startServer() {
        Configuration config = new Configuration();
        config.setPort(PORT_NUMBER);
        config.setMaxFramePayloadLength(10 * 1024 * 1024);
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
//                System.out.println(t);
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
//                    System.out.println(e);
                }
            }

        });
        server.addEventListener("send_to_user", Model_Send_Message.class, new DataListener<Model_Send_Message>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Send_Message t, AckRequest ar) throws Exception {
                sendToClient(t, ar);
            }
        });
        server.addEventListener("list_message", History.class, new DataListener<History>() {
            @Override
            public void onData(SocketIOClient sioc, History t, AckRequest ar) throws Exception {
                ar.sendAckData(serviceMessage.getHistoryMessage(t).toArray());
            }
        });
        server.addEventListener("send_file", Model_Package_Sender.class, new DataListener<Model_Package_Sender>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Package_Sender t, AckRequest ar) {
                try {
                    serviceFile.receiveFile(t);
                    ar.sendAckData(true);
                    if (t.isFinish()) {
                        Model_Receive_Image dataImage = new Model_Receive_Image();
                        dataImage.setFileID(t.getFileID());
                        Model_Send_Message message = serviceFile.closeFile(dataImage);
                        sendTempFileToClient(message, dataImage);
                    }
                } catch (IOException e) {
                    System.err.println("Error receiving chunk for fileID " + t.getFileID() + ": " + e.getMessage());
                    ar.sendAckData(false, e.getMessage());
                }
            }
        });
//        server.addEventListener("get_file", Long.class, new DataListener<Long>() {
//            @Override
//            public void onData(SocketIOClient sioc, Long t, AckRequest ar) throws Exception {
//                try {
//                    System.out.println("Requesting file info for fileID: " + t);
//                    Model_File file = serviceFile.initFile(t);
//                    long fileSize = serviceFile.getFileSize(t);
//                    String fileExtension = file.getFileExtension();
//                    if (!fileExtension.startsWith(".")) {
//                        fileExtension = "." + fileExtension;
//                    }
//                    System.out.println("Sending file info: extension=" + fileExtension + ", size=" + fileSize);
//                    ar.sendAckData(fileExtension, fileSize);
//                } catch (Exception e) {
//                    System.err.println("Error in get_file: " + e.getMessage());
//                    ar.sendAckData("error", e.getMessage());
//                }
//            }
//        });

        server.addEventListener("get_file", Long.class, new DataListener<Long>() {
            @Override
            public void onData(SocketIOClient client, Long fileID, AckRequest ar) {
                try {
                    System.out.println("Requesting file info for fileID: " + fileID);
                    Model_File file = serviceFile.initFile(fileID);
                    long fileSize = serviceFile.getFileSize(fileID);
                    String fileExtension = file.getFileExtension();
                    if (!fileExtension.startsWith(".")) {
                        fileExtension = "." + fileExtension;
                    }
//                    System.out.println("Sending file info: extension=" + fileExtension + ", size=" + fileSize);

                    ar.sendAckData(fileExtension, fileSize);
                } catch (Exception e) {
                    System.err.println("Error in get_file: " + e.getMessage());
                    ar.sendAckData("error", e.getMessage());
                }
            }
        });

        server.addEventListener("reques_file", Model_Reques_File.class, new DataListener<Model_Reques_File>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Reques_File t, AckRequest ar) {
                try {
                    System.out.println("Requesting chunk for fileID: " + t.getFileID() + ", offset: " + t.getCurrentLength());
                    byte[] data = serviceFile.getFileData(t.getCurrentLength(), t.getFileID());
                    if (data != null) {
                        String base64Chunk = Base64.getEncoder().encodeToString(data);
                        System.out.println("Sending Base64 chunk, length: " + base64Chunk.length());
                        ar.sendAckData(base64Chunk);
                    } else {
                        System.out.println("End of file for fileID: " + t.getFileID());
                        ar.sendAckData("eof");
                    }
                } catch (IOException e) {
                    System.err.println("Error in reques_file: " + e.getMessage());
                    ar.sendAckData("error", e.getMessage());
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

    private void sendToClient(Model_Send_Message data, AckRequest ar) {
        if (data.getMessageType() == MessageType.IMAGE.getValue() || data.getMessageType() == MessageType.FILE.getValue()) {
            try {
                Message file = serviceFile.addFileReceiver(data);
                serviceFile.initFile(file, data);
                ar.sendAckData(file.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (Model_Client c : listClient) {
                if (c.getUser().getUserId() == data.getToUserID()) {
                    c.getClient().sendEvent("receive_ms", new Model_Receive_Message(data.getMessageType(), data.getFromUserID(), data.getContent(), null, data.getTime()));
                    textArea.append(data.getContent() + "\n");
                    break;
                }
            }
            serviceMessage.addMessage(data);
        }
    }

    private void sendTempFileToClient(Model_Send_Message data, Model_Receive_Image dataImage) {
        for (Model_Client c : listClient) {
            if (c.getUser().getUserId() == data.getToUserID()) {
                c.getClient().sendEvent("receive_ms", new Model_Receive_Message(data.getMessageType(), data.getFromUserID(), data.getContent(), dataImage, data.getTime()));
                break;
            }
        }
    }

}
