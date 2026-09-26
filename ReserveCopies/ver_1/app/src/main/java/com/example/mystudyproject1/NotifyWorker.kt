package com.example.mystudyproject1

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.jar.Manifest

class NotifyWorker(
    context: Context,
    params: WorkerParameters
): Worker(context, params ) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        val periodNotify = NotificationCompat.Builder(
            applicationContext,
            "notificationChannel"
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Period notify")
            .setContentText("During 15 minutes you haven't created a new note")
            .build()

        NotificationManagerCompat.from(applicationContext).notify(
            2,
            periodNotify
        )
        return Result.success()
    }
}

