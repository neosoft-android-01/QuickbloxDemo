package com.webwerks.quickbloxdemo.chat.location;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.qbcore.models.LocationAttachment;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.model.ShareLocationModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 4/5/17.
 */

public class UploadLocation {

    private static Observable<File> getLocationImage(Place place) {
        LatLng latLng = place.getLatLng();
        final String locImage = "http://maps.google.com/maps/api/staticmap?markers=color:red|" +
                latLng.latitude + "," + latLng.longitude + "&zoom=16&size=370x200&sensor=true";

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

                ChatManager.getInstance().sendMessage(currentDialog, "", file, location,ChatManager.AttachmentType.LOCATION).subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ChatMessages chatMessages = (ChatMessages) o;
                        //showMessages(chatMessages);
                        App.getAppInstance().hideLoading();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
            }
        });
    }
}