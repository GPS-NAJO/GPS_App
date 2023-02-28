package com.example.gpssmsr

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class Udpsender {

    fun enviarData(ip: String, port: Int, mensaje: String){
        val ipAddress = InetAddress.getByName(ip)
        val socket = DatagramSocket()
        val data:ByteArray = mensaje.toByteArray()
        val packet = DatagramPacket(data, data.size, ipAddress, port)
        socket.send(packet)
        socket.close()
    }
}