package com.example.imageloader

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.imageloader.Constants.Glide_url
import com.example.imageloader.Constants.Retrofit_url
import com.example.imageloader.Constants.URL

class MainActivity : AppCompatActivity() {
    private var selectedNameToDownload: String? = null
    private var linkToDownload: String? = null
    private lateinit var loadingButton: LoadingButton
    private var downloadID: Long = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingButton = findViewById(R.id.download_button)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        loadingButton.setOnClickListener {
            if (linkToDownload != null) {
                loadingButton.buttonState = ButtonState.Loading
                downloadLink()
            } else
                Toast.makeText(this, "There is no item selected", Toast.LENGTH_SHORT).show()
        }
        createChannel(Constants.CHANNEL_ID, getString(R.string.notification_title))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun downloadLink() {
        val request =
            DownloadManager.Request(Uri.parse(linkToDownload))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    fun onRadioButton_Clicked(view: View) {
        if (view is RadioButton && view.isChecked) {

            when (view.getId()) {
                R.id.rd_glide ->
                    linkToDownload = Glide_url
                R.id.rd_load ->
                    linkToDownload = URL
                R.id.rd_retrofit ->
                    linkToDownload = Retrofit_url
            }

            selectedNameToDownload = view.text.toString()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)


            loadingButton.buttonState = ButtonState.Completed

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                var downloadStatus = "Fail"
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    downloadStatus = "Success"
                }

                val toast = Toast.makeText(
                    applicationContext,
                    getString(R.string.notification_description),
                    Toast.LENGTH_LONG
                )
                toast.show()

                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.sendNotification(
                    getString(R.string.notification_description),
                    applicationContext,
                    selectedNameToDownload!!,
                    downloadStatus,
                    Constants.CHANNEL_ID
                )
            }
        }
    }
}