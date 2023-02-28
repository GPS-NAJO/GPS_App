@file:Suppress("SameParameterValue")

package com.example.gpssmsr

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


var mensaje: String = "1;2;3;4"

@Suppress("KotlinConstantConditions")
class MainActivity : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#.#####")



    @SuppressLint("SetTextI18n", "UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val latitud: TextView = findViewById(R.id.latitud)
        val longitud: TextView = findViewById(R.id.longitud)
        val altitud: TextView = findViewById(R.id.altitud)
        val tiempo: TextView = findViewById(R.id.tiempo)
        val permisionAdmin = PermissionManage()
        permisionAdmin.permissionsM(this, supportFragmentManager)

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(),true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        val udp = Udpsender()
        val runnable = Runnable{

            val ip1 = findViewById<TextView>(R.id.ip1).text.toString()
            val ip2 = findViewById<TextView>(R.id.ip2).text.toString()
            val ip3 = findViewById<TextView>(R.id.ip3).text.toString()
            val ip4 = findViewById<TextView>(R.id.ip4).text.toString()
            val puerto1 = findViewById<TextView>(R.id.puerto1).text.toString().toInt()
            val puerto2 = findViewById<TextView>(R.id.puerto2).text.toString().toInt()
            val puerto3 = findViewById<TextView>(R.id.puerto3).text.toString().toInt()
            val puerto4 = findViewById<TextView>(R.id.puerto4).text.toString().toInt()

            udp.enviarData(ip1, puerto1, mensaje)
            udp.enviarData(ip2, puerto2, mensaje)
            udp.enviarData(ip3, puerto3, mensaje)
            udp.enviarData(ip4, puerto4, mensaje)
        }
            // use UDP communication
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    runnable.run()
                    delay(5000)
                }
            }
        val locationListener = LocationListener { p0 ->
            latitud.text = decimalFormat.format(p0.latitude)
            longitud.text = decimalFormat.format(p0.longitude)
            altitud.text = decimalFormat.format(p0.altitude)
            tiempo.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(p0.time))
            mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)}"
        }

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                latitud.text = "${lastKnownLocation.latitude}"
                longitud.text = "${lastKnownLocation.longitude}"
                altitud.text = "${lastKnownLocation.altitude}"
                tiempo.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(lastKnownLocation.time))
                mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};${decimalFormat.format(lastKnownLocation.longitude)}" +
                        ";${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)}"
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.00001f,locationListener)

            } else{
                latitud.text = "(x"
                longitud.text = "(x"
                altitud.text = "(x"
                tiempo.text = "(x"
            }

        }



}

