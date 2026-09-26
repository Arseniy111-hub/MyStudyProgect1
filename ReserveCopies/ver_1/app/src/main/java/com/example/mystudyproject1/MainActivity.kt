@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UNCHECKED_CAST")

package com.example.mystudyproject1

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mystudyproject1.ui.theme.MyStudyProject1Theme
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {
    val TAG = "MainActivity"

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoteDb::class.java,
            "notes.db"
        ).build()
    }

    val viewModel by viewModels<NoteViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(db.dao, application as MyApplication) as T
                }
            }
        }
    )


    val receiver = Receiver { isCharging ->
        viewModel.onIntent(NoteIntent.UpdateChargingState(isCharging))

        Toast.makeText(
            applicationContext,
            if (isCharging) getString(R.string.mobile_is_charging) else getString(R.string.mobile_is_not_charging),
            Toast.LENGTH_SHORT
        ).show()

    }




    fun handleIncomingIntent(intent: Intent) {
        if(intent.action == Intent.ACTION_SEND){
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            viewModel.onIntent(NoteIntent.HandleIncomingText(text))
            Log.d(TAG, "DATA: $text")
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = PeriodicWorkRequestBuilder<NotifyWorker>(
                15L, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(
                this@MainActivity
            ).enqueueUniquePeriodicWork(
                    "period_worker", ExistingPeriodicWorkPolicy.KEEP, request
                )



        }

        registerReceiver(
            receiver, IntentFilter(Intent.ACTION_POWER_CONNECTED)
        )
        registerReceiver(
            receiver, IntentFilter(Intent.ACTION_POWER_DISCONNECTED)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0
            )
        }
        enableEdgeToEdge()
        setContent {
            MyStudyProject1Theme {
                Navigation(viewModel)

            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if(intent.action == Intent.ACTION_SEND){
            handleIncomingIntent(intent)
        }


    }
}


