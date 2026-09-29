package com.example.mystudyproject1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Receiver(private val updateChargeState: (Boolean) -> Unit): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            Intent.ACTION_POWER_CONNECTED -> updateChargeState(true)
            Intent.ACTION_POWER_DISCONNECTED -> updateChargeState(false)
        }



    }
}