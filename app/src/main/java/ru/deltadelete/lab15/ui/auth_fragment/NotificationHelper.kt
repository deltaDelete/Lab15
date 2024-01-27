package ru.deltadelete.lab15.ui.auth_fragment

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random

class NotificationHelper(
    private val tag: String,
    @StringRes
    private val channelName: Int,
    private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    val notificationChannel = notificationManager.createNotificationChannel(
        NotificationChannelCompat.Builder(tag, 1)
            .setName(context.getString(channelName))
            .build()
    )

    fun send(notification: Notification) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(Random.nextInt(), notification)
    }
}