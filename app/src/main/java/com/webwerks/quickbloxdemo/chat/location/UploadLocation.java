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
        final String img="https://maps.googleapis.com/maps/api/staticmap?center="
                +place.getLatLng().latitude+","+place.getLatLng().longitude+
                "&zoom=16&size=600x600&markers=color:red|label:S|"+
                place.getLatLng().latitude+","+place.getLatLng().longitude;

        return Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() throws Exception {
                URL url = new URL(img);
                Bitmap bitmapImage = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                File folder = new File(Environment.getExternalStorageDirectory() + "/Chat/image");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }

                File file = new File(folder.getPath() + File.separator + "IMG_loc.png");
                OutputStream os = new FileOutputStream(file);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();

                return file;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static void sendLocation(final Context context, final ChatDialog currentDialog, final Place place){

        final String img="https://maps.googleapis.com/maps/api/staticmap?center="
                +place.getLatLng().latitude+","+place.getLatLng().longitude+
                "&zoom=16&size=600x600&markers=color:red|label:S|"+
                place.getLatLng().latitude+","+place.getLatLng().longitude;

        LocationAttachment location=new LocationAttachment();
        location.setLatitude(place.getLatLng().latitude);
        location.setLongitude(place.getLatLng().longitude);
        location.setLocationName(TextUtils.isEmpty(place.getName())?"":place.getName().toString());
        location.setLocationDesc(TextUtils.isEmpty(place.getAddress())?"":place.getAddress().toString());
        location.setUrl(img);

        SendMessageRequest sendRequest=new SendMessageRequest.Builder(currentDialog,null)
                .attachLocation(location)
                .messageType(MessageType.LOCATION).build();
        sendRequest.send().subscribe(new Consumer<Messages>() {
            @Override
            public void accept(Messages messages) throws Exception {
                (((ChatActivity)context)).showMessages(messages);
                App.getAppInstance().hideLoading();
            }
        });

        /*getLocationImage(place).subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) throws Exception {
                App.getAppInstance().showLoading(context);

                LocationAttachment location=new LocationAttachment();
                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);
                location.setLocationName(TextUtils.isEmpty(place.getName())?"":place.getName().toString());
                location.setLocationDesc(TextUtils.isEmpty(place.getAddress())?"":place.getAddress().toString());

                SendMessageRequest sendRequest=new SendMessageRequest.Builder(currentDialog,null)
                        .attachMedia(file)
                        .attachLocation(location)
                        .messageType(MessageType.LOCATION).build();
                sendRequest.send().subscribe(new Consumer<Messages>() {
                    @Override
                    public void accept(Messages messages) throws Exception {
                        (((ChatActivity)context)).showMessages(messages);
                        App.getAppInstance().hideLoading();
                    }
                });
            }
        });*/
    }
}
