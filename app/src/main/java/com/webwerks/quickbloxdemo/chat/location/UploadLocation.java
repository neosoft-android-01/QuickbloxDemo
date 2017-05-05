package com.webwerks.quickbloxdemo.chat.location;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.SendMessageRequest;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.models.LocationAttachment;
import com.webwerks.quickbloxdemo.chat.ChatActivity;
import com.webwerks.quickbloxdemo.global.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 4/5/17.
 */

public class UploadLocation {

    private static Observable<File> getLocationImage(Place place) {
        LatLng latLng = place.getLatLng();
        /*final String locImage = "http://maps.google.com/maps/api/staticmap?markers=color:red|" +
                latLng.latitude + "," + latLng.longitude + "&zoom=12&size=600x400&sensor=true";*/

        final String locImage="https://maps.googleapis.com/maps/api/staticmap?center="
                +latLng.latitude+","+latLng.longitude+"&markers=color:red|&zoom=12&size=600x400";

        return Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                URL url = new URL(locImage);
                Bitmap bitmapImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }

                File file = new File(folder.getPath() + File.separator + "IMG_temp_loc.png");
                OutputStream os = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                return file;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static void sendLocation(final Context context, final ChatDialog currentDialog, final Place place){
        getLocationImage(place).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                App.getAppInstance().showLoading(context);

                LocationAttachment location=new LocationAttachment();
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                location.setLocationName(TextUtils.isEmpty(place.getName())?"":place.getName().toString());
                location.setLocationDesc(TextUtils.isEmpty(place.getAddress())?"":place.getAddress().toString());

                Observer progressObserver = new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer value) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                };

                SendMessageRequest sendRequest=new SendMessageRequest.Builder(currentDialog,progressObserver)
                        .attachLocation(location)
                        .messageType(MessageType.LOCATION).build();
                sendRequest.send().subscribe(new Consumer<Messages>() {
                    @Override
                    public void accept(Messages messages) throws Exception {
                        (((ChatActivity)context)).showMessages(messages);
                        App.getAppInstance().hideLoading();
                    }
                });

                /*ChatManager.getInstance().sendMessage(currentDialog, "", file, location,ChatManager.AttachmentType.LOCATION).subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Messages chatMessages = (Messages) o;
                        //showMessages(chatMessages);
                        App.getAppInstance().hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });*/
            }
        });
    }
}
