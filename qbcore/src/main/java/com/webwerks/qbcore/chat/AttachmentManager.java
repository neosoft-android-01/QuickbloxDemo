package com.webwerks.qbcore.chat;

import com.quickblox.content.QBContent;
import com.quickblox.core.exception.QBResponseException;

import java.io.InputStream;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Created by webwerks on 28/4/17.
 */

public class AttachmentManager {

    public static Observable<InputStream> processReceivedAttachment(final String fileId){

        return Observable.fromCallable(new Callable<InputStream>() {
            @Override
            public InputStream call() throws Exception {
                try {
                    return QBContent.downloadFile(fileId).perform();
                } catch (QBResponseException e) {
                    e.printStackTrace();
                    throw new Exception("File not found");
                }
            }
        });
    }

}
