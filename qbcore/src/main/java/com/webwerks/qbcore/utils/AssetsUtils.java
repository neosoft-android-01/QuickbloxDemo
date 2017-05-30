package com.webwerks.qbcore.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.webwerks.qbcore.models.QbConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by webwerks on 5/4/17.
 */

public class AssetsUtils {

    public static QbConfig getQbConfigurationFromAssets(String filename, Context context){
        QbConfig qbConfig=null;
        try {
            String config=AssetsUtils.getJsonStringFromFile(filename,context);
            qbConfig= new Gson().fromJson(config,QbConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return qbConfig;
    }

    public static String getJsonStringFromFile(String filename, Context context) throws IOException {
        AssetManager assetManager=context.getAssets();
        StringBuilder stringBuilder=new StringBuilder();
        InputStream json=assetManager.open(filename);
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(json,"UTF-8"));
        String str;
        while ((str=bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}
