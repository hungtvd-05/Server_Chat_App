package com.app.model;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model_File_Sender {

    private Model_File data;
    private File file;
    private RandomAccessFile accFile;
    private long fileSize;

    public Model_File_Sender(Model_File data, File file) throws IOException {
        this.data = data;
        this.file = file;
        this.accFile = new RandomAccessFile(file, "r");
        this.fileSize = accFile.length();
    }

    public byte[] read(long currentLength) throws IOException {
        accFile.seek(currentLength);
        if (currentLength != fileSize) {
            int max = 2000;
            long length = currentLength + max >= fileSize ? fileSize - currentLength : max;
            byte[] b = new byte[(int) length];
            accFile.read(b);
            return b;
        } else {
            return null;
        }
    }
}
