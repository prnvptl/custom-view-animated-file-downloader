package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.ui.DetailActivity

// Notification ID.
private const val NOTIFICATION_ID = 0
const val FILE_NAME = "FILE_NAME"
const val STATUS = "STATUS"

/**
 * Builds and delivers the notification.
 */
@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    fileName: String,
    status: Boolean,
    applicationContext: Context
) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(FILE_NAME, fileName)
    contentIntent.putExtra(STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_ONE_SHOT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.app_notif_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(fileName)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(-1, applicationContext.getString(R.string.check_status), contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
