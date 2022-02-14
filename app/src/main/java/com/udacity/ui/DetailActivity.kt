package com.udacity.ui

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.FILE_NAME
import com.udacity.R
import com.udacity.STATUS
import com.udacity.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()
        val status = intent.getBooleanExtra(STATUS, false)

        file_name_value.apply {
            text = intent.getStringExtra(FILE_NAME)
        }
        status_value.apply {
            text = if(status) "Success" else "Failed"
            setTextColor(if(status) getColor(R.color.colorPrimaryDark) else Color.RED)
        }

        submit_ok.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}
