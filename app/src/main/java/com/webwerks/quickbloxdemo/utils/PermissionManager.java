package com.webwerks.quickbloxdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by webwerks on 27/4/17.
 * Handler for managing permission for android OS 6 and above
 */
public class PermissionManager {

    // check permission
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    // Ask permission from activity context
    public static boolean askForPermission(final int reqCode,final Activity activity,
                                           final String permission,String accessMessage) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission( activity, permission);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale( activity, permission)) {
                ActivityCompat.requestPermissions( activity, new String[] {permission}, reqCode);
            } else {
                ActivityCompat.requestPermissions( activity, new String[]{permission}, reqCode);
            }
        } else {
            return true;
        }
        return false;
    }

    // Ask multiple permissions from fragment context
    public static boolean askForPermissions(final int reqCode,final Fragment fragment,
                                           final String permission[],String accessMessage) {
        List<String> permissions=new ArrayList<>();
        boolean hasAll = true;
        for(String per:permission){
            if(!hasPermission(fragment.getActivity(), per)){
                permissions.add(per);
                hasAll = false;
            }
        }

        if(hasAll){
            return true;
        }

        fragment.requestPermissions(permissions.toArray(new String[permissions.size()]), reqCode);
        return false;
    }

    // Ask multiple permissions from Activity context
    public static boolean askForPermissions(final int reqCode,final Activity activity,
                                            final String permission[],String accessMessage) {

        List<String> permissions=new ArrayList<>();
        boolean hasAll = true;
        for(String per:permission){
            if(!hasPermission(activity, per)){
                permissions.add(per);
                hasAll = false;
            }
        }

        if(hasAll){
            return true;
        }

        ActivityCompat.requestPermissions(activity,permissions.toArray(new String[permissions.size()]), reqCode);
        return false;
    }

    // Ask permission from fragment context
    public static boolean askForPermission(final int reqCode,final Fragment fragment,
                                           final String permission,String accessMessage) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission( fragment.getActivity(), permission);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                /*showMessageOKCancel(activity,accessMessage,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[] {permission},
                                        reqCode);
                            }
                        });*/
                fragment.requestPermissions(new String[] {permission},reqCode);
            } else {
                fragment.requestPermissions(new String[]{permission}, reqCode);
            }
        } else {
            return true;
        }
        return false;
    }


    private static void showMessageOKCancel(Context context,String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder( context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
