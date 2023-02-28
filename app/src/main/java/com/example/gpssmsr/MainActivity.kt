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
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


var mensaje: String = "HOLA MUNDO"

@Suppress("KotlinConstantConditions")
class MainActivity : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#.###")



    @SuppressLint("SetTextI18n", "UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val boton: Button = findViewById(R.id.button)
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
        val port1 = 52022
        //val port2 = 51012
        //val port3 = 51000
        //val ipAddress = InetAddress.getByName("aflorez.sytes.net")
        //val ipAddress2 = InetAddress.getByName("191.109.23.244")
        //val data: ByteArray = mensaje.toByteArray()
        val runnable = Runnable{
            udp.enviarData("191.109.14.205",port1, mensaje)
        }
        boton.setOnClickListener {
            // use UDP communication
            CoroutineScope(Dispatchers.IO).launch {
                runnable.run()
            }
        }
        val locationListener = LocationListener { p0 ->
            latitud.text = "Latitud: ${decimalFormat.format(p0.latitude)}"
            longitud.text = "Longitud: ${decimalFormat.format(p0.longitude)}"
            altitud.text = "Altitud: ${decimalFormat.format(p0.altitude)}"
            tiempo.text = "tiempo: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(p0.time))}"
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

