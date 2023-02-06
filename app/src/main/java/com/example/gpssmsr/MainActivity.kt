package com.example.gpssmsr

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class PermissionLocationExplanationDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
            .setMessage("Este permiso es necesario, la app necesita acceder a tu localización para poder funcionar")
            .setPositiveButton("Ok"){ _, _ ->
                ActivityCompat.requestPermissions(this.requireActivity(),arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            .setNegativeButton("Cancel"){ _, _ ->
                activity?.finish()
            }
        return builder.create()
    }
}

class PermissionSMSExplanationDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
            .setMessage("Este permiso es necesario, la app necesita acceder a tu servicio de mensajeria para poder funcionar")
            .setPositiveButton("Ok"){ _, _ ->
                ActivityCompat.requestPermissions(this.requireActivity(),arrayOf(Manifest.permission.SEND_SMS), 2)
            }
            .setNegativeButton("Cancel"){ _, _ ->
                activity?.finish()
            }
        return builder.create()
    }
}

class MainActivity : AppCompatActivity() {

    val decimalFormat = DecimalFormat("#.###")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val switchEnable: SwitchCompat = findViewById(R.id.switch2)
        val dialogLocation = PermissionLocationExplanationDialog()
        val dialogSMS = PermissionSMSExplanationDialog()
        val button: Button = findViewById(R.id.button)
        var mensaje: String? = null
        val smsManager = getSystemService(SmsManager::class.java)
        val latitud: TextView = findViewById(R.id.latitud)
        val longitud: TextView = findViewById(R.id.longitud)
        val altitud: TextView = findViewById(R.id.altitud)
        val tiempo: TextView = findViewById(R.id.tiempo)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object: LocationListener {
            @SuppressLint("SetTextI18n")
            override fun onLocationChanged(location: Location) {
                latitud.text = "Latitud: ${decimalFormat.format(location.latitude)}"
                longitud.text = "Longitud: ${decimalFormat.format(location.longitude)}"
                altitud.text = "Altitud: ${decimalFormat.format(location.altitude)}"
                tiempo.text = "tiempo ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(location.time))}"
                mensaje = "${decimalFormat.format(location.latitude)};${decimalFormat.format(location.longitude)};${decimalFormat.format(location.altitude)};${decimalFormat.format(location.time)}"
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(Provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED){
            // Permission not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                dialogSMS.show(supportFragmentManager, "PermissionSMSExplanationDialog")
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    2)
            }}
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                dialogLocation.show(supportFragmentManager, "PermissionLocationExplanationDialog")

            } else {
                // Request access
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }}
            button.setOnClickListener {
                if (mensaje != null) {
                    smsManager.sendTextMessage("+573006335532", null, mensaje, null, null)
                    smsManager.sendTextMessage("+573209459098", null, mensaje, null, null)
                    smsManager.sendTextMessage("+573003632142", null, mensaje, null, null)
                }
            }
            switchEnable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        0f,
                        locationListener
                    )
                } else {
                    locationManager.removeUpdates(locationListener)
                }
            }
        }
    }

