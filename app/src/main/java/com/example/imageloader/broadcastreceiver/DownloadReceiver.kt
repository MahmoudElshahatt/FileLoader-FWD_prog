package com.example.imageloader.broadcastreceiver

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.imageloader.utils.ButtonState
import com.example.imageloader.utils.CustomButton
import com.example.imageloader.R
import com.example.imageloader.constants.Constants
import com.example.imageloader.sendNotification

//I made a BroadCast Receiver instead of object expression.
class DownloadReceiver(val customButton: CustomButton) : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Range")
    override fun onReceive(p0: Context?, p1: Intent?) {
        val downLoudId = p1?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

        customButton.buttonState = ButtonState.Completed

        val downloadManager =
            p0!!.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val qu = DownloadManager.Query()
        qu.setFilterById(downLoudId!!)

        val cursor = downloadManager.query(qu)

        if (cursor.moveToFirst()) {

            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

            var downloadStatus = "Fail"
            if (status == DownloadManager.STATUS_SUCCESSFUL)
                downloadStatus = "Success"

            Constants.STATUS = downloadStatus
            //Sending Notification about the download status
            val notificationManager = p0!!.getSystemService(NotificationManager::class.java)
            notificationManager.sendNotification(
                p0.getString(R.string.notification_description),
                p0,
                Constants.FILENAME,
                Constants.STATUS,
                Constants.CHANNEL_ID
            )
        }
    }

}