package com.webwerks.quickbloxdemo.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.webwerks.qbcore.chat.AttachmentManager;
import com.webwerks.qbcore.models.MessageType;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;

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
                    imageFile = new File(folder.getPath() + File.separator + "IMG_" + timeStamp + ".png");
                if (imageFile.createNewFile()) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    public static File getAudioFile() {
        //File folder = null;
        File file = null;
        try {
            File folderAudio = new File(Environment.getExternalStorageDirectory() + "/Chat/audio");
            boolean successAudio = true;
            if (!folderAudio.exists()) {
                successAudio = folderAudio.mkdirs();
            }

            if (successAudio) {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                file = new File(folderAudio.getPath() + File.separator + "AUD_" + timeStamp + ".mp3");
                if (file.createNewFile()) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File getAudioFile(int fileId){
        File folderAudio = new File(Environment.getExternalStorageDirectory() + "/Chat/audio");
        boolean successAudio = true;
        if (!folderAudio.exists()) {
            successAudio = folderAudio.mkdirs();
        }
        return new File(folderAudio.getPath()+File.separator+"AUD_"+fileId+".mp3");
    }

    public static File getImageFile(int fileId){
        File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        return new File(folder.getPath() + File.separator + "IMG_" + fileId + ".png");
    }

    public static Observable<String> getAttachmentPath(int fileId, MessageType type){
        File file=null;
        switch (type){
            case IMAGE:
                file=getImageFile(fileId);
                break;

            case AUDIO:
                file=getAudioFile(fileId);
                break;
        }


        return AttachmentManager.processReceivedAttachment(fileId,file);

        /*return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                File file = new File(folder.getPath() + File.separator + "IMG_" + fileId + ".png");

                if(file.exists()){
                    return file.getPath();
                }else{
                    try {
                        InputStream in =null;
                        int responseCode = -1;
                        URL url = new URL(fileUrl);
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setDoInput(true);
                        con.connect();
                        responseCode = con.getResponseCode();
                        if(responseCode == HttpURLConnection.HTTP_OK) {
                            //download
                            in = con.getInputStream();
                            OutputStream output = new FileOutputStream(file);
                            IOUtils.copy(in, output);

                            output.close();
                            return file.getPath();
                        }else{
                            Log.e("ERROR",streamToString(con.getErrorStream()));
                            return con.getErrorStream().toString();
                        }
                    }catch (Exception e){
                        throw new Exception(e.getMessage());
                    }
                }
            }
        })*/

        /*AttachmentManager.processReceivedAttachment(fileId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).map(new Function<InputStream, String>() {
            @Override
            public String apply(InputStream inputStream) throws Exception {

                try {
                    if(inputStream!=null) {

                        File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        File file = new File(folder.getPath() + File.separator + "IMG_" + fileId + ".png");
                        OutputStream output = new FileOutputStream(file);
                        IOUtils.copy(inputStream, output);

                        output.close();
                        return file.getPath();
                    }else{
                        return "";
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // handle exception, define IOException and others
                    throw new Exception(e.getMessage());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());*/

    }

    public static MediaData getPath(final Context context, final Uri uri) {
        MediaData mediaData = new FileUtil().new MediaData();

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {

                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        mediaData.setType(getMimeType(context, uri));
                        mediaData.setPath(Environment.getExternalStorageDirectory() + "/" + split[1]);

                        return mediaData;
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    mediaData.setPath(getDataColumn(context, contentUri, null, null, mediaData));
                    mediaData.setType(getMimeType(context, contentUri));

                    return mediaData;
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    mediaData.setPath(getDataColumn(context, contentUri, selection, selectionArgs, mediaData));
                    mediaData.setType(getMimeType(context, contentUri));
                    return mediaData;
                }
            }else if ("content".equalsIgnoreCase(uri.getScheme())) {
                mediaData.setPath(getDataColumn(context, uri, null, null, mediaData));
                mediaData.setType(getMimeType(context, uri));

                return mediaData;
            }else if ("file".equalsIgnoreCase(uri.getScheme())) {
                mediaData.setPath(uri.getPath());
                mediaData.setType(getMimeType(context, uri));
                return mediaData;
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            mediaData.setPath(getDataColumn(context, uri, null, null, mediaData));
            mediaData.setType(getMimeType(context, uri));

            return mediaData;
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            mediaData.setPath(uri.getPath());
            mediaData.setType(getMimeType(context, uri));
            return mediaData;
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs,MediaData data) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column/*,MediaStore.Images.Media.MIME_TYPE*/};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndex(column);
                if(column_index>=0) {
                    // data.setType(cursor.getString(cursor.getColumnIndex(projection[1])));
                    return cursor.getString(column_index);
                }else{
                    return null;
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getMimeType(Context context,Uri uri){
        String type = null;

        ContentResolver cR = context.getContentResolver();
        type=cR.getType(uri);

        if(TextUtils.isEmpty(type)){
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        Log.e("Content Type",type + " ::::");

        return type;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public class MediaData{

        private String type;
        private String path;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
