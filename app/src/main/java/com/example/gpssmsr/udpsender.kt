package com.example.gpssmsr

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class Udpsender {

    fun enviarData(ip: String, port: Int, mensaje: String){
        var mensajee = mensaje
        val ch = '.'
        if (mensajee.indexOf(ch) >= 0) {
            mensajee=mensajee.replace(",",".")
        }
        val ipAddress = InetAddress.getByName(ip)
        val socket = DatagramSocket()
        val data:ByteArray = mensajee.toByteArray()
        val packet = DatagramPacket(data, data.size, ipAddress, port)

        socket.send(packet)
        socket.close()
    }
}