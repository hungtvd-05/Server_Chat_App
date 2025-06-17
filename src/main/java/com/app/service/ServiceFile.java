package com.app.service;

import com.app.dao.MessageDAO;
import com.app.model.Message;
import com.app.model.Model_File;
import com.app.model.Model_File_Receiver;
import com.app.model.Model_File_Sender;
import com.app.model.Model_Package_Sender;
import com.app.model.Model_Receive_Image;
import com.app.model.Model_Send_Message;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ServiceFile {

    private MessageDAO messageDAO;

    public ServiceFile() {
        messageDAO = new MessageDAO();
        this.fileReceivers = new HashMap<>();
        this.fileSenders = new HashMap<>();
    }

    public Message addFileReceiver(Model_Send_Message data) {
        Message ms = new Message(
                data.getMessageType(),
                data.getFromUserID(),
                data.getToUserID(),
                data.getEncryptedContent(),
                data.getSignature(),
                data.getEncryptedAESKey(),                
                data.getFileExtension(),
                data.getBlurHash(),
                data.getHeight_blur(),
                data.getWidth_blur(),
                LocalDateTime.parse(data.getTime())
        );
        System.out.println(ms);
        return messageDAO.saveandgetMessage(ms).join();
    }

    public void initFile(Message file, Model_Send_Message data) throws IOException {
        fileReceivers.put(file.getId(), new Model_File_Receiver(data, toFileObject(file)));
    }

    public Model_File getFile(Long fileID) {
        Message ms = messageDAO.getFile(fileID).join();
        return new Model_File(fileID, ".enc");
    }

    public synchronized Model_File initFile(Long fileID) throws IOException {
        Model_File file;
        if (!fileSenders.containsKey(fileID)) {
            file = getFile(fileID);
            fileSenders.put(fileID, new Model_File_Sender(file, new File(PATH_FILE + fileID + ".enc")));
        } else {
            file = fileSenders.get(fileID).getData();
        }
        return file;
    }

    public byte[] getFileData(long currentLength, Long fileID) throws IOException {
        initFile(fileID);
        return fileSenders.get(fileID).read(currentLength);
    }

    public long getFileSize(Long fileID) {
        return fileSenders.get(fileID).getFileSize();
    }

    public void receiveFile(Model_Package_Sender dataPackage) throws IOException {
        if (!dataPackage.isFinish()) {
            fileReceivers.get(dataPackage.getFileID()).writeFile(Base64.getDecoder().decode(dataPackage.getData()));
        } else {
            fileReceivers.get(dataPackage.getFileID()).close();
        }
    }

    public Model_Send_Message closeFile(Long fileID) throws IOException {
        Model_File_Receiver file = fileReceivers.get(fileID);
        fileReceivers.remove(fileID);
        file.getMessage().setId(fileID);
                
        return file.getMessage();
    }

    private File toFileObject(Message file) {
        return new File(PATH_FILE + file.getId() + ".enc");
    }

    private final String PATH_FILE = "server_data/";

    private final Map<Long, Model_File_Receiver> fileReceivers;
    private final Map<Long, Model_File_Sender> fileSenders;
}
