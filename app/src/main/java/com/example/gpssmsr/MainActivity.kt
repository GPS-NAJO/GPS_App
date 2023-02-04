package com.example.gpssmsr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val switchEnable: SwitchCompat = findViewById(R.id.switch2)
        val latitud: TextView = findViewById(R.id.latitud)
        val longitud: TextView = findViewById(R.id.longitud)
        val altitud: TextView = findViewById(R.id.altitud)
        val tiempo: TextView = findViewById(R.id.tiempo)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object: LocationListener {
            @SuppressLint("SetTextI18n")
            override fun onLocationChanged(location: Location) {
                latitud.text = "La: ${location.latitude}"
                longitud.text = "Lo: ${location.longitude}"
                altitud.text = "Al: ${location.altitude}"
                tiempo.text = "timestamp ${location.time}"
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(Provider: String, status: Int, extras: Bundle) {}

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        switchEnable.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1)
                    }
                } else {
                    // Permission has already been granted
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0f, locationListener)
                }
            } else {
                locationManager.removeUpdates(locationListener)
            }
        }






    }

}
