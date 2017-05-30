package com.webwerks.quickbloxdemo.global;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Window;

import com.webwerks.qbcore.auth.QBInitialization;
import com.webwerks.qbcore.models.User;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.utils.SharedPrefUtils;


/**
 * Created by webwerks on 5/4/17.
 */

public class App extends Application {

    private static App appInstance;
    private User currentUser=null;

    public User getCurrentUser() {
        if(currentUser==null)
            currentUser=SharedPrefUtils.getInstance().getQbUser();
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        SharedPrefUtils.getInstance().saveQbUser(currentUser);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance=this;
        QBInitialization.initializationQb(this);
    }

    public static App getAppInstance(){
        if(appInstance==null)
            appInstance=new App();
        return appInstance;
    }

    Dialog alertDialog;

    public void showLoading(Context context){
        alertDialog = new Dialog(context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_loader);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void hideLoading(){
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private DisplayMetrics disMat;

    public DisplayMetrics getDisplayMatrix(){
        if(disMat==null)
            disMat = getAppInstance().getResources().getDisplayMetrics();

        return disMat;
    }
}
