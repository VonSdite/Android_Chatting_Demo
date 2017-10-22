package com.sdite.innovate.Demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(Activity whitchActivitySendQuit){
        AlertDialog.Builder dialog = new AlertDialog.Builder(whitchActivitySendQuit);
        dialog.setTitle("你确定要退出吗");
        dialog.setCancelable(false);
        dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (Activity activity:activities){
                    if(!activity.isFinishing()){
                        activity.finish();
                    }
                }
                activities.clear();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }
}
