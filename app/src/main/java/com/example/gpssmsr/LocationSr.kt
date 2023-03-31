package com.example.gpssmsr

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
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
import java.text.DecimalFormat


class LocationSr: Service() {
    private val decimalFormat = DecimalFormat("#.#####")

    private lateinit var locationListener: LocationListener // define locationListener as a member variable

    private var mensaje: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(),true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        val udp = Udpsender()
        mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};${decimalFormat.format(lastKnownLocation.longitude)}" +
                ";${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)}"
        mensaje = mensaje.replace(',','.')


        val locationListener = LocationListener { p0 ->
            mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)}"
            mensaje = mensaje.replace(',','.')
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            udp.enviarData("52.4.150.68",1001, mensaje)
            udp.enviarData("44.212.144.254",1001,mensaje)
            udp.enviarData("44.194.192.186",1001, mensaje)
            udp.enviarData("84.239.15.140",1001,mensaje)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.00001f,locationListener)
        }
        // create a notification channel for Android Oreo and higher

            val channel = NotificationChannel(
                "location_channel_id",
                "Location Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)

        // create a notification
        val notification = NotificationCompat.Builder(this, "location_channel_id")
            .setContentTitle("Location Service")
            .setContentText("Location Service is running in the background.")
            .build()

        // start the service in the foreground with the notification
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(locationListener) // use the member variable here
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}
