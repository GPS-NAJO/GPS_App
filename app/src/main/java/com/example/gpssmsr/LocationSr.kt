package com.example.gpssmsr

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.lang.Runnable
import java.text.DecimalFormat

class LocationSr: Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val decimalFormat = DecimalFormat("#.#####")
    private var mensaje: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(), true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        val id = Identity.getUUID(applicationContext)
        val udp = Udpsender()


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationListener = LocationListener { p0 ->
                mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};" +
                        "${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)};${id}"
                mensaje = mensaje.replace(',', '.')
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0.00001f, locationListener)


            mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};" +
                    decimalFormat.format(lastKnownLocation.longitude) +
                    ";${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)};${id}"
            mensaje = mensaje.replace(',', '.')


            val runnable = Runnable {
                udp.enviarData("52.4.150.68", 1001, mensaje)
                udp.enviarData("44.212.144.254", 1001, mensaje)
                udp.enviarData("44.194.192.186", 1001, mensaje)
                udp.enviarData("84.239.15.140", 1001, mensaje)
            }


            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    runnable.run()
                    delay(5000)
                }
            }

        }
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location")
            .setContentText("Location is been send")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)

        startForeground(1, notification.build())

    }
    private fun stop() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    override fun onDestroy() {
        serviceScope.cancel()

    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

}






