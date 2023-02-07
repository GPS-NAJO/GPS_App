@file:Suppress("SameParameterValue")

package com.example.gpssmsr

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

    var caso: Int = 0
    var mensaje: String? = null
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val smsManager = context.getSystemService(SmsManager::class.java)
        if (mensaje != null) {
            smsManager.sendTextMessage("+573006335532", null, mensaje, null, null)
            smsManager.sendTextMessage("+573209459098", null, mensaje, null, null)
            smsManager.sendTextMessage("+573003632142", null, mensaje, null, null)
        }
    }
}
class PermissionExplanationDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog  {

            val builder = AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
                .setMessage("Es necesario tener los permisos de mensajeria y localización para que la app pueda funcionar")
                .setNegativeButton("Cancel") { _, _ ->

                }
        when (caso) {
            1 -> {
                builder.setPositiveButton("Ok") { _, _ ->
                    requestPermissions(this.requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS),1)
                }
            }
            2 -> {
                builder.setPositiveButton("Ok") { _, _ ->
                    requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.SEND_SMS), 2)
                }
            }
            3 -> {
                builder.setPositiveButton("ok"){ _, _ ->
                    requestPermissions(this.requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),3)
                }
            }
        }
            return builder.create()
        }
}

class MainActivity : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#.###")


    @SuppressLint("SetTextI18n", "UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dialogpermissions = PermissionExplanationDialog()
        val latitud: TextView = findViewById(R.id.latitud)
        val longitud: TextView = findViewById(R.id.longitud)
        val altitud: TextView = findViewById(R.id.altitud)
        val tiempo: TextView = findViewById(R.id.tiempo)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = getBroadcast(this,5,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val provider = locationManager.getBestProvider(Criteria(),true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        val interval: Long = 60 * 1000
        val locationListener = object: LocationListener {


            override fun onLocationChanged(p0: Location) {
            latitud.text = "Latitud: ${decimalFormat.format(p0.latitude)}"
            longitud.text = "Longitud: ${decimalFormat.format(p0.longitude)}"
            altitud.text = "Altitud: ${decimalFormat.format(p0.altitude)}"
            tiempo.text = "tiempo ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(p0.time))}"
            mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)}"
            }



        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            caso = 1
            // Permission not granted on SMS and Location
            if(shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)
                && shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
            ) {

                dialogpermissions.show(supportFragmentManager, "PermissionExplanationDialog")
            } else {
                requestPermissions(this,
                    arrayOf(Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION),
                    101)
            }
        } else if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            caso = 2
            // Permission not granted on SMS
            if(shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)){

                dialogpermissions.show(supportFragmentManager, "PermissionExplanationDialog")
            } else {
                requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS),102)
            }
        } else if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            caso = 3
            // Permission not granted on Location
            if(shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                dialogpermissions.show(supportFragmentManager,"PermissionExplanationDialog")
            } else {
                requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),103)
            }
        }


            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                latitud.text = "Latitud: ${lastKnownLocation.latitude}"
                longitud.text = "Longitud: ${lastKnownLocation.longitude}"
                altitud.text = "Altitud: ${lastKnownLocation.altitude}"
                tiempo.text = "tiempo ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(lastKnownLocation.time))}"
                mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};${decimalFormat.format(lastKnownLocation.longitude)};" +
                        "${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)}"
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.0001f,locationListener)

            } else{
                latitud.text = "(x"
                longitud.text = "(x"
                altitud.text = "(x"
                tiempo.text = "(x"
            }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval,pendingIntent)
        }
    private fun getBroadcast(context: Context?, id: Int, intent: Intent?, flag: Int): PendingIntent {
        return if (VERSION.SDK_INT >= VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, id, intent!!, PendingIntent.FLAG_MUTABLE or flag)
        } else {
            PendingIntent.getBroadcast(context, id, intent!!, flag)
        }
    }
    }

