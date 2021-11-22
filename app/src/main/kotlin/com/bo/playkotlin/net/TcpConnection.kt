package com.bo.playkotlin

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun startTcp() {
    runBlocking {
        val job = launch(Dispatchers.IO) { startTcpServer() }
        job.start()

        val clientJob = launch(Dispatchers.IO) { 
            startTcpClient() 
        }

        clientJob.start()
        job.join()
        clientJob.join()
    }
}

fun startUdp() {
    runBlocking {
        val job = launch(Dispatchers.IO) {startUdpServer()}
        val clientJob = launch(Dispatchers.IO) {startUdpClient()}

        job.join()
        clientJob.join()
    }
}

//////////////////
// tcp server & client
suspend fun startTcpServer() = coroutineScope {
    var server = ServerSocket(8081)

    var socket = server.accept()
    var buffer = BufferedReader(InputStreamReader(socket.inputStream))
    var output = PrintWriter(socket.outputStream)
    try {
        println("server listening...")
        yield()
        val line = buffer.readLine()
        println("accept: $line")
        output.println("reback to you.")
        output.flush()
    } finally {
        output.close()
        buffer.close()
        socket.close()
        server.close()
    }
}

suspend fun startTcpClient() = coroutineScope {
    println("client listening...")
    var socket = Socket("127.0.0.1", 8081)
    val printer = PrintWriter(socket.outputStream)
    printer.println("call server No.1")
    printer.flush()

    val reader = BufferedReader(InputStreamReader(socket.inputStream))
    println(reader.readLine())

    printer.close()
    reader.close()
    socket.close()
}

////////////////
// udp server & client
suspend fun startUdpServer() = coroutineScope {
    var socket = DatagramSocket(8081)
    var buffer = ByteArray(1024)
    var packet = DatagramPacket(buffer, buffer.size)
    socket.receive(packet)

    val charset = Charsets.UTF_8

    println("upd info: \r\n ip: ${packet.address.hostAddress} \r\n port: ${packet.port} \r\n data:${packet.data.toString(charset)}")
    socket.close()
}

suspend fun startUdpClient() = coroutineScope {
    var socket = DatagramSocket()
    var buffer = ByteArray(1024)

    var packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName("127.0.0.1"), 8081)
    packet.data = "wa hahawa".toByteArray()

    socket.send(packet)
    socket.close()
}