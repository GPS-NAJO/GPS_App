@file:Suppress("SameParameterValue")

package com.example.gpssmsr

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.Socket
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

var mensaje: String = "HOLA MUNDO"
class PermissionExplanationDialog : DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog  {

            val builder = AlertDialog.Builder(requireContext()).setTitle("Permission Denied")
                .setMessage("Es necesario tener los permisos de Internet y localización para que la app pueda funcionar")
                .setNegativeButton("Cancel") { _, _ -> }
                .setPositiveButton("Ok") { _, _ ->
                    requestPermissions(
                        this.requireActivity(), arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), 1
                    )
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

        val boton: Button = findViewById(R.id.button)
        val latitud: TextView = findViewById(R.id.latitud)
        val longitud: TextView = findViewById(R.id.longitud)
        val altitud: TextView = findViewById(R.id.altitud)
        val tiempo: TextView = findViewById(R.id.tiempo)

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // Permission not granted on SMS and Location
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                dialogpermissions.show(supportFragmentManager, "PermissionDialog")
            } else {
                requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101)
            }
        }

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(),true)
        val lastKnownLocation: Location = provider.let { locationManager.getLastKnownLocation(it!!)!! }
        val port1 = 52022
        val port2 = 51012
        val ipAddress = InetAddress.getByName("191.109.12.168")
        //val ipAddress2 = InetAddress.getByName("192.168.1.17")
        var data: ByteArray
        val socket = DatagramSocket()
        var socket2 : Socket
        var packet: DatagramPacket
        val runnable = Runnable{
            // port = portOb.text.toString().toInt()
            data = mensaje.toByteArray()
            packet = DatagramPacket(data, data.size, ipAddress, port1)
            socket.send(packet)
        }
        val runnable2 = Runnable {
            // port = portOb.text.toString().toInt()
            data = mensaje.toByteArray()
            socket2 = Socket(ipAddress, port2)
            val outputStream = socket2.getOutputStream()
            outputStream.write(data)
            outputStream.flush()
        }
        boton.setOnClickListener {
            // use TCP communication
            CoroutineScope(Dispatchers.IO).launch {
                runnable.run()
                runnable2.run()
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
                data = mensaje.toByteArray()
                packet = DatagramPacket(data, data.size, ipAddress, port1)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0.00001f,locationListener)

            } else{
                latitud.text = "(x"
                longitud.text = "(x"
                altitud.text = "(x"
                tiempo.text = "(x"
            }

        }



}

