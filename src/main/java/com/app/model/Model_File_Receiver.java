package com.app.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model_File_Receiver {
    Model_Send_Message message;
    File file;
    RandomAccessFile accFile;

    public Model_File_Receiver(Model_Send_Message message, File file) throws FileNotFoundException {
        this.message = message;
        this.file = file;
        this.accFile = new RandomAccessFile(file, "rw");
    }
    
    public synchronized long writeFile(byte[] data) throws IOException {
        accFile.seek(accFile.length());
        accFile.write(data);
        return accFile.length();
    }
    
    public void close() throws IOException {
        accFile.close();
    }
}
