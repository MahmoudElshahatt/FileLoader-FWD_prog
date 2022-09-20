package com.example.imageloader


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    message: String,
    applicationContext: Context,
    fileName: String,
    status: String,
    channelId: String
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra("status", status)
    contentIntent.putExtra("filename", fileName)

    val pendingIntent = PendingIntent.getActivity(
        applicationContext, NOTIFICATION_ID,
        contentIntent, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.ic_cloud_download)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(message)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_cloud_download,
            "Check the changes",
            pendingIntent
        ) .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())


}