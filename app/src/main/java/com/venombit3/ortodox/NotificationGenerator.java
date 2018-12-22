package com.venombit3.ortodox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class NotificationGenerator {

    public static final String NOTIFY_PREVIOUS = "com.venombit3.ortodox.previous";
    public static final String NOTIFY_DELETE = "com.venombit3.ortodox.delete";
    public static final String NOTIFY_PAUSE = "com.venombit3.ortodox.pause";
    public static final String NOTIFY_PLAY = "com.venombit3.ortodox.play";
    public static final String NOTIFY_NEXT = "com.venombit3.ortodox.next";

    private static void setListeners(RemoteViews view, Context context) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.drawable.butoninapoi, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.drawable.remove, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.drawable.butonpausa, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.drawable.butoninainte, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.drawable.butonplay, pPlay);
    }



    static String songName = "Now You're Gone";
    static String albumName = "Now Youre Gone - The Album";

    public static void customBigNotification(Context context) {
        RemoteViews expandedView = new RemoteViews(context.getPackageName(), R.layout.big_notification);

        Notification notification = new NotificationCompat.Builder(context,"1")
                .setSmallIcon(R.drawable.cross)
                .setContentTitle("Ortodox").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.bigContentView = expandedView;
        notification.bigContentView.setTextViewText(R.id.textSongName, songName);
        notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);


        setListeners(expandedView, context);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }



}
