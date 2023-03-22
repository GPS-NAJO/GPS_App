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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat


class LocationSr: Service() {
    private val decimalFormat = DecimalFormat("#.#####")

    private lateinit var locationManager: LocationManager
    private lateinit var udp: Udpsender
    private lateinit var runnable: Runnable
    private var mensaje: String = ""

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(),true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        udp = Udpsender()
        val port1 = 52022
        mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};${decimalFormat.format(lastKnownLocation.longitude)}" +
                ";${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)}"
        runnable = Runnable{
            udp.enviarData("191.109.14.205",port1, mensaje)
            udp.enviarData("201.185.177.60",52000,mensaje)
        }
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                runnable.run()
                delay(5000)
            }
        }
        val locationListener = LocationListener { p0 ->
            mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)}"
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.00001f,locationListener)
        }
    }
}