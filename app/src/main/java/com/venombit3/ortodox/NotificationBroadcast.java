package com.venombit3.ortodox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationBroadcast extends BroadcastReceiver {
    public static final String NOTIFY_PREVIOUS = "com.venombit3.ortodox.previous";
    public static final String NOTIFY_DELETE = "com.venombit3.ortodox.delete";
    public static final String NOTIFY_PAUSE = "com.venombit3.ortodox.pause";
    public static final String NOTIFY_PLAY = "com.venombit3.ortodox.play";
    public static final String NOTIFY_NEXT = "com.venombit3.ortodox.next";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NOTIFY_PLAY)) {
            Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NOTIFY_PAUSE)) {
            Toast.makeText(context, "NOTIFY_PAUSE", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NOTIFY_NEXT)) {
            Toast.makeText(context, "NOTIFY_NEXT", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(NOTIFY_DELETE)) {
            Toast.makeText(context, "NOTIFY_DELETE", Toast.LENGTH_LONG).show();
        }else if (intent.getAction().equals(NOTIFY_PREVIOUS)) {
            Toast.makeText(context, "NOTIFY_PREVIOUS", Toast.LENGTH_LONG).show();
        }
    }
}