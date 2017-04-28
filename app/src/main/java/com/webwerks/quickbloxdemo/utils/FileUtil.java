package com.webwerks.quickbloxdemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.webwerks.qbcore.chat.AttachmentManager;
import com.webwerks.qbcore.chat.OnAttachmentDownload;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 27/4/17.
 */
public class FileUtil {

    public static File getImageFile() {
        File folder = null;
        File imageFile = null;
        try {
            folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }

            if (success) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    imageFile = new File(folder.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
                if (imageFile.createNewFile()) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    public static Observable<String> getImageAttachmentPath(final String fileId){

        return AttachmentManager.processReceivedAttachment(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).map(new Function<InputStream, String>() {
            @Override
            public String apply(InputStream inputStream) throws Exception {

                try {
                    File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
                    boolean success = true;
                    if (!folder.exists()) {
                        success = folder.mkdirs();
                    }
                    File file=new File(folder.getPath() + File.separator + "IMG_" + fileId + ".jpeg");

                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    FileOutputStream out=new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,90,out);

                   /* OutputStream output = new FileOutputStream(file);
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                    output.close();*/
                    return file.getPath();
                } catch (Exception e) {
                    e.printStackTrace(); // handle exception, define IOException and others
                    throw new Exception(e.getMessage());
                }

                /*if(inputStream!=null){
                    FileOutputStream fos=null;
                    BufferedOutputStream bos=null;
                    try{
                        File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }

                        File file=new File(folder.getPath() + File.separator + "IMG_" + fileId + ".jpeg");
                        fos=new FileOutputStream(file);
                        bos=new BufferedOutputStream(fos);
                        byte[] aByte = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(aByte)) != -1) {
                            bos.write(aByte, 0, bytesRead);
                        }
                        bos.flush();
                        bos.close();

                        return file.getPath();
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new Exception(e.getMessage());
                    }
                }else{
                    return "";
                }*/
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
