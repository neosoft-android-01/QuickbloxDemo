package com.webwerks.qbcore.chat;

/**
 * Created by webwerks on 28/4/17.
 */

public interface OnAttachmentDownload {

    void onDownloadComplete(String path);
    void onDownloadError(String error);
    void onDownloadProgress(int i);
}
