package com.app.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileReceiver {
    private final FileOutputStream fos;
    private final File file;

    public FileReceiver(File file) throws IOException {
        this.file = file;
        this.fos = new FileOutputStream(file);
    }

    public synchronized void writeFile(byte[] data) throws IOException {
        fos.write(data);
    }

    public synchronized void close() throws IOException {
        fos.flush();
        fos.close();
    }

    public FileOutputStream getFileOutputStream() {
        return fos;
    }

    public File getFile() {
        return file;
    }
}
