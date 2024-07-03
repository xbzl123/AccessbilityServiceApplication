package com.raysharp.accessbilityserviceapplication

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

/**
 * Copyright (c) 2022 Raysharp.cn. All rights reserved
 *
 * GameNotification
 * @author longyanghe
 * @date 2022-06-28
 */
object GameNotification {
    private var manager: NotificationManager? = null
    private var remoteViews: RemoteViews? = null
    var notify: Notification? = null

    fun createNotificaton(context: Context) {
        manager = context.getSystemService(AccessibilityService.NOTIFICATION_SERVICE) as NotificationManager

        // 设置通知栏的图片文字
        remoteViews = RemoteViews(
            context.packageName,
            R.layout.custom_notice
        )
        remoteViews!!.setImageViewResource(R.id.widget_play_0, R.drawable.ic_baseline_play_arrow_1)
        remoteViews!!.setImageViewResource(R.id.widget_play_1, R.drawable.ic_baseline_play_arrow)
        remoteViews!!.setImageViewResource(R.id.widget_play, R.drawable.ic_baseline_play_arrow_24)
        remoteViews!!.setImageViewResource(R.id.widget_stop, R.drawable.ic_baseline_stop_24)

        val builder = NotificationCompat.Builder(context)
        var PendingIntentFlag = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntentFlag = PendingIntent.FLAG_IMMUTABLE;
        }
        val intent = Intent(context, MainActivity::class.java)
        // 点击跳转到主界面
        val intent_go = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntentFlag
        )
        remoteViews!!.setOnClickPendingIntent(R.id.notice, intent_go)

        // 设置开始按键
        val start = Intent()
        start.action = Command.ACTION_START
        val intent_start = PendingIntent.getBroadcast(
            context, 0, start,
            PendingIntentFlag
        )
        remoteViews!!.setOnClickPendingIntent(R.id.widget_play, intent_start)

        // 设置开始按键1
        val start1 = Intent()
        start1.action = Command.ACTION_MODEL_CHANGE
        val intent_start1 = PendingIntent.getBroadcast(
            context, 2, start1,
            PendingIntentFlag
        )
        remoteViews!!.setOnClickPendingIntent(R.id.widget_play_1, intent_start1)


        // 设置开始按键0
        val start0 = Intent()
        start1.action = Command.ACTION_MODEL_CHANGE_1
        val intent_start0 = PendingIntent.getBroadcast(
            context, 3, start1,
            PendingIntentFlag
        )
        remoteViews!!.setOnClickPendingIntent(R.id.widget_play_0, intent_start0)
        // 设置停止按键
        val stop = Intent()
        stop.action = Command.ACTION_STOP
        val intent_stop = PendingIntent.getBroadcast(
            context, 1, stop,
            PendingIntentFlag
        )
        remoteViews!!.setOnClickPendingIntent(R.id.widget_stop, intent_stop)
        builder.setSmallIcon(R.drawable.ic_launcher_background) // 设置顶部图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel("123", "147", NotificationManager.IMPORTANCE_LOW)
            manager!!.createNotificationChannel(mChannel)
            notify = Notification.Builder(context)
                .setChannelId("123")
                //                    .setContentTitle("5 new messages")
                //                    .setContentText("hahaha")
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.ic_launcher).build()
        } else {
            notify = builder.build()
            notify!!.contentView = remoteViews // 设置下拉图标
            notify!!.bigContentView = remoteViews // 防止显示不完全,需要添加apisupport
            notify!!.flags = Notification.FLAG_ONGOING_EVENT
        }
        manager!!.notify(100, notify)
    }

}