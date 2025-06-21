package com.app.service;

import com.app.app.MessageType;
import com.app.model.History;
import com.app.model.Message;
import com.app.model.Model_Client;
import com.app.model.Model_File;
import com.app.model.Model_Login;
import com.app.model.Model_Message;
import com.app.model.Model_Package_Sender;
import com.app.model.Model_Public_Key;
import com.app.model.Model_Receive_Image;
import com.app.model.Model_Register;
import com.app.model.Model_Reques_File;
import com.app.model.Model_Send_Message;
import com.app.model.Model_Update_User;
import com.app.model.TestUserAccount;
import com.app.model.User;
import com.app.util.MailUtil;
import com.app.util.Utils;
import com.app.util.Validator;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JTextArea;

public class Service {

    private static Service instance;
    private SocketIOServer server;
    private ServiceUser serviceUser;
    private ServiceMessage serviceMessage;
    private ServiceFile serviceFile;
    private ServiceOtp serviceOtp;
//    private List<Model_Client> listClient;
    private Map<SocketIOClient, TestUserAccount> clientMap;

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
        serviceUser.updateAllStatus();
        serviceOtp = new ServiceOtp();
        clientMap = new ConcurrentHashMap<>();
    }

    public void startServer() {
        Configuration config = new Configuration();
        config.setPort(PORT_NUMBER);
        config.setMaxFramePayloadLength(10 * 1024 * 1024);
        config.setPingTimeout(30000);
        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient sioc) {
                textArea.append("One client connection\n");
            }
        });
        
        
        
        server.addEventListener("init_register", Model_Register.class, new DataListener<Model_Register>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Register t, AckRequest ar) throws Exception {
                
                
                if (serviceUser.doesUsernameOrEmailExist(t.getUserName(), t.getMail())) {
                    ar.sendAckData(false,"Invalid username or mail");
                    return;
                };
                   
                if (!Validator.isValidUsername(t.getUserName()) || false) {
                    ar.sendAckData(false,"Invalid username");
                    return;
                }
                
                if (!Validator.isValidFullName(t.getFullName())) {
                    ar.sendAckData(false,"Invalid fullname");
                    return;
                }
                
                if (!Validator.isValidEmail(t.getMail()) || false) {
                    ar.sendAckData(false,"Invalid mail");
                    return;
                }
                
                if (!Validator.isValidPhone(t.getPhone())) {
                    ar.sendAckData(false,"Invalid phone");
                    return;
                }
                
                if (!Validator.isValidPassword(t.getPassword())) {
                    ar.sendAckData(false,"Invalid password");
                    return;
                }
                
                String otp = Utils.generateOTP(6);
                while (!serviceOtp.saveOtp(t.getUserName(), otp, LocalDateTime.now().plusMinutes(10))) {
                    otp = Utils.generateOTP(6);
                    System.out.println(otp);
                }   
                
                MailUtil.sendOTP(t.getMail(), otp);
                
                ar.sendAckData(true);
            }
        });
        
        server.addEventListener("register", Model_Register.class, new DataListener<Model_Register>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Register t, AckRequest ar) throws Exception {
                Model_Message message = serviceUser.register(t);
                ar.sendAckData(message.isAction(), message.getMessage(), message.getData());
                  
                if (message.isAction()) {
                    TestUserAccount medata = (TestUserAccount) message.getData();
                    server.getBroadcastOperations().sendEvent("list_user", medata);
                    addClient(sioc, medata);
                    userConnect(medata.getUserId());
                }
            }

        });
        
        server.addEventListener("edit_account", Model_Update_User.class, new DataListener<Model_Update_User>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Update_User t, AckRequest ar) throws Exception {

                TestUserAccount user = clientMap.get(sioc);

                if (!user.getUserName().equals(t.getUserName())) {
                    ar.sendAckData(false, "Username mismatch. Unauthorized request.");
                    return;
                }

                if (!Validator.isValidFullName(t.getFullName())) {
                    ar.sendAckData(false, "Full name must be at least 2 characters.");
                    return;
                }

                if (!Validator.isValidEmail(t.getMail())) {
                    ar.sendAckData(false, "Invalid email format.");
                    return;
                }

                if (!Validator.isValidPhone(t.getPhone())) {
                    ar.sendAckData(false, "Phone number must be 9 to 11 digits.");
                    return;
                }

                if (t.getCurrentPassword().isEmpty() && t.getNewPassword().isEmpty() && t.getConfirmNewPassword().isEmpty()) {
                    user.update(t);
                    serviceUser.updateUserAccountInfo(user);
                    ar.sendAckData(true, user);
                } else {
                    if (!serviceUser.checkPassword(t.getUserName(), t.getCurrentPassword())) {
                        ar.sendAckData(false, "Current password is incorrect.");
                        return;
                    }

                    if (!Validator.isValidPassword(t.getNewPassword())) {
                        ar.sendAckData(false, "New password must be at least 6 characters and contain both letters and digits.");
                        return;
                    }

                    if (!Validator.isPasswordConfirmed(t.getNewPassword(), t.getConfirmNewPassword())) {
                        ar.sendAckData(false, "New password and confirmation do not match.");
                        return;
                    }

                    serviceUser.changePassword(t.getUserName(), t.getNewPassword());
                    user.update(t);
                    serviceUser.updateUserAccountInfo(user);
                    clientMap.replace(sioc, user);
                    ar.sendAckData(true, user);
                    }
                    clientMap.replace(sioc, user);
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
//                    userConnect(login.getUserId());
                } else {
                    textArea.append("Mot may da dang nhap that bai\n");
                    ar.sendAckData(false);
                }
            }
        });
        server.addEventListener("change_key", Model_Public_Key.class, new DataListener<Model_Public_Key>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Public_Key t, AckRequest ar) throws Exception {
                serviceUser.changePublicKey(t);
                userConnect(t.getUserID());
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
                System.out.println(t);
                
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
                        textArea.append("da nhan anh thanh cong");
                        Model_Send_Message message = serviceFile.closeFile(t.getFileID());
                        sioc.sendEvent("file_completed", message);
                        sendTempFileToClient(message);
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
                    Model_File file = serviceFile.initFile(fileID);
                    long fileSize = serviceFile.getFileSize(fileID);
                    String fileExtension = file.getFileExtension();
                    ar.sendAckData(fileExtension, fileSize);
                } catch (Exception e) {
                    ar.sendAckData("error", e.getMessage());
                }
            }
        });
        
        server.addEventListener("get_message_by_id", Long.class, new DataListener<Long>() {
            @Override
            public void onData(SocketIOClient client, Long fileID, AckRequest ar) {
                System.out.println("yeu cau id " + fileID);
                Message ms = serviceMessage.getMessageById(fileID);
                System.out.println(ms);
                ar.sendAckData(new Model_Send_Message(
                        ms.getId(),
                        ms.getMessageType(),
                        ms.getFromUserID(),
                        ms.getToUserID(),
                        ms.getEncryptedContent(),
                        ms.getSignature(),
                        ms.getEncryptedAESKey(),
                        ms.getPublicKeyDSAFromUser(),
                        ms.getFileExtension(),
                        ms.getBlurHash(),
                        ms.getHeight_blur(),
                        ms.getWidth_blur(),
                        ms.getTime().toString()
                ));
            }
        });

        server.addEventListener("reques_file", Model_Reques_File.class, new DataListener<Model_Reques_File>() {
            @Override
            public void onData(SocketIOClient sioc, Model_Reques_File t, AckRequest ar) {
                try {
                    byte[] data = serviceFile.getFileData(t.getCurrentLength(), t.getFileID());
                    if (data != null) {
                        String base64Chunk = Base64.getEncoder().encodeToString(data);
                        ar.sendAckData(base64Chunk);
                    } else {
                        ar.sendAckData("eof");
                    }
                } catch (IOException e) {
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
        
        server.addEventListener("update_status", TestUserAccount.class, new DataListener<TestUserAccount>() {
            @Override
            public void onData(SocketIOClient sioc, TestUserAccount t, AckRequest ar) throws Exception {
                addClient(sioc, t);
                userConnect(t.getUserId());
            }
        });
        
        server.start();
        textArea.append("Server has start on port: " + PORT_NUMBER + "\n");
    }

    private void userConnect(long userID) {
        serviceUser.updateStatus(true, userID);
        User user = serviceUser.getUserById(userID);
        if (user != null) {
            server.getBroadcastOperations().sendEvent("user_status", Long.valueOf(userID), true, user.getUserAccount().getPubkeyDSA(), user.getUserAccount().getPubkeyRSA());
        }
    }

    private void userDisconnect(long userID) {
        serviceUser.updateStatus(false, userID);
        server.getBroadcastOperations().sendEvent("user_status", Long.valueOf(userID), false, "", "");
    }

    private void addClient(SocketIOClient client, TestUserAccount user) {
        clientMap.put(client, user);
    }

    public long removeClient(SocketIOClient client) {
        TestUserAccount removed = clientMap.remove(client);
        return removed != null ? removed.getUserId() : 0;
    }

    private void sendToClient(Model_Send_Message data, AckRequest ar) {
        if (data.getMessageType() == MessageType.IMAGE.getValue() || data.getMessageType() == MessageType.FILE.getValue()) {
            try {
                Message file = serviceFile.addFileReceiver(data);
                data.setPubkeyDSAFromUser(file.getPublicKeyDSAFromUser());
                serviceFile.initFile(file, data);
                ar.sendAckData(file.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Message saveMessage = serviceMessage.addMessage(data);
            
            ar.sendAckData(saveMessage.getId());

            for (Map.Entry<SocketIOClient, TestUserAccount> entry : clientMap.entrySet()) {
                if (entry.getValue().getUserId() == data.getToUserID()) {
                    data.setId(saveMessage.getId());
                    data.setPubkeyDSAFromUser(saveMessage.getPublicKeyDSAFromUser());
                    entry.getKey().sendEvent("receive_ms", data);
                    textArea.append(data.getEncryptedContent() + "\n");
                    break;
                }
            }
        }
    }

    private void sendTempFileToClient(Model_Send_Message data) {
        for (Map.Entry<SocketIOClient, TestUserAccount> entry : clientMap.entrySet()) {
            if (entry.getValue().getUserId() == data.getToUserID()) {
                entry.getKey().sendEvent("receive_ms", data);
                break;
            }
        }
    }

}
