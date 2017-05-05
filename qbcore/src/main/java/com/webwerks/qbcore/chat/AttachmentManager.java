package com.webwerks.qbcore.chat;

import android.util.Log;

import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 28/4/17.
 */

public class AttachmentManager {

    public static synchronized Observable<String> processReceivedAttachment(final int fileId, final File path){

        if(path.exists()){
            return Observable.fromCallable(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return path.getPath();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }else{
            return Observable.fromCallable(new Callable<InputStream>() {
                @Override
                public InputStream call() throws Exception {
                    return QBContent.downloadFileById(fileId, new QBProgressCallback() {
                        @Override
                        public void onProgressUpdate(int i) {

                        }
                    }).perform();
                }
            }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).map(new Function<InputStream, String >() {
                @Override
                public String apply(InputStream inputStream) throws Exception {

                    OutputStream output = new FileOutputStream(path);
                    IOUtils.copy(inputStream, output);

                    output.close();
                    return path.getPath();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        }
    }

    public static synchronized Observable<QBFile> uploadMediaAttachment(final File path, final Observer<Integer> progressUpdate){

        return Observable.fromCallable(new Callable<QBFile>(){
            @Override
            public QBFile call() throws Exception {
                return QBContent.uploadFileTask(path, false, "", new QBProgressCallback() {
                    @Override
                    public void onProgressUpdate(final int i) {
                        Observable.create(new ObservableOnSubscribe<Integer>() {

                            @Override
                            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                                e.onNext(i);

                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(progressUpdate);

                        Log.e("Progress",":::" + i);
                    }
                }).perform();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

}
