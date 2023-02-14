@file:Suppress("SameParameterValue")

package com.example.gpssmsr

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

var caso: Int = 0
var mensaje: String = "HOLA MUNDO"
class PermissionExplanationDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog  {

            val builder = AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
                .setMessage("Es necesario tener los permisos de Internet y localización para que la app pueda funcionar")
                .setNegativeButton("Cancel") { _, _ ->

                }
        when (caso) {
            1 -> {
                builder.setPositiveButton("Ok") { _, _ ->
                    requestPermissions(this.requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET),1)
                }
            }
            2 -> {
                builder.setPositiveButton("Ok") { _, _ ->
                    requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.INTERNET), 2)
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

@Suppress("KotlinConstantConditions")
class MainActivity : AppCompatActivity() {
    private val decimalFormat = DecimalFormat("#.###")



    @SuppressLint("SetTextI18n", "UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dialogpermissions = PermissionExplanationDialog()
        val ipAddressOb: EditText = findViewById(R.id.ipAdress)
        val protocolo: ToggleButton = findViewById(R.id.PROTOCOL)
        val portOb : EditText = findViewById(R.id.Port)
        val latitud: TextView = findViewById(R.id.latitud)
        val longitud: TextView = findViewById(R.id.longitud)
        val altitud: TextView = findViewById(R.id.altitud)
        val tiempo: TextView = findViewById(R.id.tiempo)
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(),true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        var port = portOb.text.toString().toInt()
        var ipAddress = InetAddress.getByName(ipAddressOb.text.toString())
        var data: ByteArray
        val socket = DatagramSocket()
        var socket2 : Socket
        var packet: DatagramPacket
        val runnable = Runnable{
            port = portOb.text.toString().toInt()
            ipAddress = InetAddress.getByName(ipAddressOb.text.toString())
            data = mensaje.toByteArray()
            packet = DatagramPacket(data, data.size, ipAddress, port)
            socket.send(packet)
        }
        val runnable2 = Runnable {
            port = portOb.text.toString().toInt()
            ipAddress = InetAddress.getByName(ipAddressOb.text.toString())
            data = mensaje.toByteArray()
            socket2 = Socket(ipAddress, port)
            val outputStream = socket2.getOutputStream()
            outputStream.write(data)
            outputStream.flush()
        }
        protocolo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // use TCP communication
                CoroutineScope(Dispatchers.IO).launch{
                    while(isChecked) {
                        runnable.run()
                        delay(timeMillis = 9000)
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch{
                    while(!isChecked) {
                        runnable2.run()
                        delay(timeMillis = 9000)
                    }
                }
            }
        }

        val locationListener = LocationListener { p0 ->
            latitud.text = "Latitud: ${decimalFormat.format(p0.latitude)}"
            longitud.text = "Longitud: ${decimalFormat.format(p0.longitude)}"
            altitud.text = "Altitud: ${decimalFormat.format(p0.altitude)}"
            tiempo.text = "tiempo: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(p0.time))}"
            mensaje = "${decimalFormat.format(p0.latitude)};${decimalFormat.format(p0.longitude)};${decimalFormat.format(p0.altitude)};${decimalFormat.format(p0.time)}"
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            caso = 1
            // Permission not granted on SMS and Location
            if(shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                && shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
            ) {

                dialogpermissions.show(supportFragmentManager, "PermissionDialog")
            } else {
                requestPermissions(this,
                    arrayOf(Manifest.permission.INTERNET,Manifest.permission.ACCESS_FINE_LOCATION),
                    101)
            }
        } else if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            caso = 2
            // Permission not granted on SMS
            if(shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)){

                dialogpermissions.show(supportFragmentManager, "PermissionDialog")
            } else {
                requestPermissions(this, arrayOf(Manifest.permission.INTERNET),102)
            }
        } else if(ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            caso = 3
            // Permission not granted on Location
            if(shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){

                dialogpermissions.show(supportFragmentManager,"PermissionDialog")
            } else {
                requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),103)
            }
        }
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                latitud.text = "${lastKnownLocation.latitude}"
                longitud.text = "${lastKnownLocation.longitude}"
                altitud.text = "${lastKnownLocation.altitude}"
                tiempo.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault()).format(Date(lastKnownLocation.time))
                mensaje = "${decimalFormat.format(lastKnownLocation.latitude)};${decimalFormat.format(lastKnownLocation.longitude)}" +
                        ";${decimalFormat.format(lastKnownLocation.altitude)};${decimalFormat.format(lastKnownLocation.time)}"
                data = mensaje.toByteArray()
                packet = DatagramPacket(data, data.size, ipAddress, port)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.00001f,locationListener)

            } else{
                latitud.text = "(x"
                longitud.text = "(x"
                altitud.text = "(x"
                tiempo.text = "(x"
            }

        }



}

