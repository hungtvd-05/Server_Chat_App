package com.app.service;

import com.app.app.MessageType;
import com.app.dao.MessageDAO;
import com.app.model.Message;
import com.app.model.Model_File;
import com.app.model.Model_File_Receiver;
import com.app.model.Model_File_Sender;
import com.app.model.Model_Package_Sender;
import com.app.model.Model_Receive_Image;
import com.app.model.Model_Send_Message;
import com.app.swing.blurHash.BlurHash;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

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
                LocalDateTime.parse(data.getTime())
        );
        return messageDAO.saveandgetMessage(ms).join();
    }

    public void initFile(Message file, Model_Send_Message data) throws IOException {
        fileReceivers.put(file.getId(), new Model_File_Receiver(data, toFileObject(file)));
    }

    public Model_File getFile(Long fileID) {
        Message ms = messageDAO.getFile(fileID).join();
        return new Model_File(fileID, ms.getFileExtension());
    }

    public synchronized Model_File initFile(Long fileID) throws IOException {
        Model_File file;
        if (!fileSenders.containsKey(fileID)) {
            file = getFile(fileID);
            fileSenders.put(fileID, new Model_File_Sender(file, new File(PATH_FILE + fileID + file.getFileExtension())));
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
            fileReceivers.get(dataPackage.getFileID()).writeFile(dataPackage.getData());
        } else {
            fileReceivers.get(dataPackage.getFileID()).close();
        }
    }

    public Model_Send_Message closeFile(Model_Receive_Image dataImage) throws IOException {
        Model_File_Receiver file = fileReceivers.get(dataImage.getFileID());
        if (file.getMessage().getMessageType() == MessageType.IMAGE.getValue()) {
            file.getMessage().setEncryptedContent("");
            file.getMessage().setEncryptedAESKey("");
            file.getMessage().setSignature("");
            convertFileToBlurHash(file.getFile(), dataImage);
            messageDAO.updateBlur(dataImage.getFileID(), dataImage.getImage(), dataImage.getHeight(), dataImage.getWidth());
        }
        fileReceivers.remove(dataImage.getFileID());
        file.getMessage().setId(dataImage.getFileID());
        file.getMessage().setBlurHash(dataImage.getImage());
        file.getMessage().setHeight_blur(dataImage.getHeight());
        file.getMessage().setWidth_blur(dataImage.getWidth());
        return file.getMessage();
    }

    private void convertFileToBlurHash(File file, Model_Receive_Image dataImage) throws IOException {
        BufferedImage img = ImageIO.read(file);
        Dimension size = getAutoSize(new Dimension(img.getWidth(), img.getHeight()), new Dimension(200, 200));
        BufferedImage newImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        g2.drawImage(img, 0, 0, size.width, size.height, null);
        String blurhash = BlurHash.encode(newImage);
        dataImage.setWidth(size.width);
        dataImage.setHeight(size.height);
        dataImage.setImage(blurhash);
    }

    private Dimension getAutoSize(Dimension fromSize, Dimension toSize) {
        int w = toSize.width;
        int h = toSize.height;
        int iw = fromSize.width;
        int ih = fromSize.height;
        double xScale = (double) w / iw;
        double yScale = (double) h / ih;
        double scale = Math.min(xScale, yScale);
        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        return new Dimension(width, height);
    }

    private File toFileObject(Message file) {
        return new File(PATH_FILE + file.getId() + file.getFileExtension());
    }

    private final String PATH_FILE = "server_data/";

    private final Map<Long, Model_File_Receiver> fileReceivers;
    private final Map<Long, Model_File_Sender> fileSenders;
}
