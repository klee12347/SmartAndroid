/*
 * Copyright (c) 2014.
 * This Project and its content is copyright of ftc
 * All rights reserved.
 */

package fpg.ftc.si.smart.util;

import org.apache.http.entity.InputStreamEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 可以掛載監聽器的InputeStream類
 * Ref:http://www.hardill.me.uk/wordpress/2011/09/03/android-notification-uploading-progressbar/
 * Created by MarlinJoe on 2014/5/11.
 */
public class ProgressInputStreamEntity extends InputStreamEntity {
    private UploadListener listener;
    private long length;

    public ProgressInputStreamEntity(InputStream instream, long length) {
        super(instream, length);
        this.length = length;
    }

    public void setUploadListener(UploadListener listener) {
        this.listener = listener;
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        super.writeTo(new CountingOutputStream(outstream));
    }

    class CountingOutputStream extends OutputStream {
        private long counter = 0l;
        private OutputStream outputStream;

        public CountingOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int oneByte) throws IOException {
            this.outputStream.write(oneByte);
            counter++;
            if (listener != null) {
                int percent = (int) ((counter * 100)/ length);
                listener.onChange(percent);
            }
        }
    }

    public interface UploadListener {
        public void onChange(int percent);
    }
}
