package com.gz.repair;

import android.app.Application;

import com.gz.repair.bean.Login;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Endeavor on 2016/8/26.
 */
public class MyAppcation extends Application {

    public static String baseUrl = "http://ceshi.sx-soft.net/android_oa";
    public static int userId = -1 ;
    public static int rootId = -1 ;
    public static String userName  ;
//    public static boolean isSuccess = true  ;
    public static String clientid   ;
    public static ArrayList<Login.Result.Privileges>  pro= new ArrayList<Login.Result.Privileges>();
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
    }
}
