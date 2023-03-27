package com.example.gpssmsr

import android.content.Context
import android.content.Intent
import androidx.work.*
import java.util.concurrent.TimeUnit

class BackGround {
    fun runTask(context: Context) {
        val intent = Intent(context, MainActivity::class.java)     // Código que se ejecutará en segundo plano
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)            // En lugar de llamar al constructor de MainActivity,
        context.startActivity(intent)                           // crea una intención para iniciar la actividad
    }
}

class BackgroundWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        BackGround().runTask(applicationContext)
        return Result.success()
    }
}

fun enqueueBackgroundWorker(context: Context) {
    val periodicWorkRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(6, TimeUnit.SECONDS)
        .build()


    WorkManager.getInstance(context).enqueue(periodicWorkRequest)
}



