package com.example.imageloader

import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi

class DetailActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
        val fileNameText = findViewById<TextView>(R.id.file_name_text)
        val statusText = findViewById<TextView>(R.id.state)

        val fileName = intent.getStringExtra("filename")
        fileNameText.text = fileName

        val status = intent.getStringExtra("status")

        if (status == "Success") {
            statusText.setTextColor(Color.parseColor("#00FF00"))
        } else {
            statusText.setTextColor(Color.parseColor("#FF0000"))
        }
        statusText.text = status

    }

    fun Button_Clicked(view: View) {
        finish()
    }
}