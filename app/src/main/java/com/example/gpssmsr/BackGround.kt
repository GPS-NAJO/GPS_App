package com.example.gpssmsr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat


class BackGround {
        fun runTask(context: Context) {
            val decimalFormat = DecimalFormat("#.#####")
            val permisionAdmin = PermissionManage()
            permisionAdmin.permissionsM(context.applicationContext as AppCompatActivity, (context as AppCompatActivity).supportFragmentManager)
            val id = Identity.getUUID(context)
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(),true)
            val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
            val udp = Udpsender()
            var mensaje: String = ""

            val runnable = Runnable {
                val ip1 = "34.239.15.140"
                val ip2 = "52.4.150.68"
                val ip3 = "44.194.192.186"
                val ip4 = "44.212.144.254"
                val puerto1 = 1001
                val puerto2 = 1001
                val puerto3 = 1001
                val puerto4 = 1001
                udp.enviarData(ip1, puerto1, mensaje)
                udp.enviarData(ip2, puerto2, mensaje)
                udp.enviarData(ip3, puerto3, mensaje)
                udp.enviarData(ip4, puerto4, mensaje)
            }

            //use UDP communication
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    runnable.run()
                    delay(5000)
                }
            }
            val locationListener = LocationListener { p0 ->
                mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};" +
                        "${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)};${id}"
                mensaje=mensaje.replace(",",".")
            }

            if(ContextCompat.checkSelfPermission(context.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};${decimalFormat.format(lastKnownLocation.longitude)}" +
                        ";${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)};${id}"
                mensaje=mensaje.replace(",",".")
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.00001f,locationListener)


            }

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
