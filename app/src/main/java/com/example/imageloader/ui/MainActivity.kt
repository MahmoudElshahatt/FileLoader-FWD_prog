package com.example.imageloader.ui


import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.imageloader.utils.ButtonState
import com.example.imageloader.utils.CustomButton
import com.example.imageloader.R
import com.example.imageloader.broadcastreceiver.DownloadReceiver
import com.example.imageloader.constants.Constants
import com.example.imageloader.constants.Constants.Glide_url
import com.example.imageloader.constants.Constants.Retrofit_url
import com.example.imageloader.constants.Constants.URL


class MainActivity : AppCompatActivity() {
    private var downloadLink: String = ""
    lateinit var customButton: CustomButton
    private var downloadID: Long = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customButton = findViewById(R.id.download_button)

        val broadcastReceiver = DownloadReceiver(customButton)
        registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rd_glide ->
                    downloadLink = Glide_url
                R.id.rd_load ->
                    downloadLink = URL
                R.id.rd_retrofit ->
                    downloadLink = Retrofit_url
            }
            //Testing RadioGroup Listener.
            // Toast.makeText(applicationContext, downloadLink+"", Toast.LENGTH_SHORT).show()

            //Getting the file name from radio button.
            val radio: RadioButton? = findViewById(checkedId)
            Constants.FILENAME = radio?.text.toString()
        }

        customButton.setOnClickListener {
            customButton.buttonState = ButtonState.Clicked
            if (!downloadLink.isEmpty()) {
                //Updating the state of the custom button.
                customButton.buttonState = ButtonState.Loading
                //Calling my func to start downloading.
                downloadLink()
            } else
                Toast.makeText(this, "There is no item selected", Toast.LENGTH_SHORT).show()
        }

        createChannel(Constants.CHANNEL_ID, getString(R.string.notification_title))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun downloadLink() {
        //Creating a request to start the download.
        val request =
            DownloadManager.Request(Uri.parse(downloadLink))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    //Creating the channel for the notification.
    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}