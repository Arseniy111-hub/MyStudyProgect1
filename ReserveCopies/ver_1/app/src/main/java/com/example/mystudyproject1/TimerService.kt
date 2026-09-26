package com.example.mystudyproject1


import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Chronometer
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat


class TimerService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun start(){

        val notification = NotificationCompat.Builder(this, "notificationChannel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Timer")
            .setContentText("Focus session running")
            .setWhen(System.currentTimeMillis())
            .setUsesChronometer(true)
            .build()

        startForeground(1, notification)
    }
    enum class Actions{
        START,
        STOP
    }
}