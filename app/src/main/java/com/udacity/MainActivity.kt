package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

enum class DownloadSource(val url: String) {
    GLIDE("https://github.com/bumptech/glide"),
    PROJECT("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"),
    RETROFIT("https://github.com/square/retrofit")
}

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private var selectedDownloadSrc: DownloadSource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = getSystemService(NotificationManager::class.java)
        createChannel(
            getString(R.string.app_notif_channel_id),
            getString(R.string.notification_title)
        )
        custom_button.setOnClickListener {
            if (selectedDownloadSrc == null) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.select_file),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                selectedDownloadSrc?.let { src ->
                    download(src)
                }
            }
        }
    }

    override fun onStart() {
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(receiver)
        super.onStop()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val q = DownloadManager.Query()
            id?.let { q.setFilterById(id) }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val c: Cursor = downloadManager.query(q)
            if (c.moveToFirst()) {
                val status: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE))
                notificationManager.sendNotification(
                    title,
                    status == DownloadManager.STATUS_SUCCESSFUL,
                    context
                )
            }
            custom_button.updateState(ButtonState.Completed)
            c.close()
        }
    }

    private fun download(source: DownloadSource) {
        custom_button.updateState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(source.url))
                .setTitle(getTitleForSrc(source))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getTitleForSrc(source: DownloadSource): String {
        return when (source) {
            DownloadSource.GLIDE -> getString(R.string.glide_download)
            DownloadSource.PROJECT -> getString(R.string.load_app_download)
            DownloadSource.RETROFIT -> getString(R.string.retrofit_download)
        }
    }

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked
        if (checked) {
            selectedDownloadSrc = when (view.id) {
                R.id.glideSrc -> DownloadSource.GLIDE
                R.id.projectSrc -> DownloadSource.PROJECT
                R.id.retrofitSrc -> DownloadSource.RETROFIT
                else -> null
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { setShowBadge(false) }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_description)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
