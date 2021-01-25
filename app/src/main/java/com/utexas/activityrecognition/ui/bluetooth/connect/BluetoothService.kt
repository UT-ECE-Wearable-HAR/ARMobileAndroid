package com.utexas.activityrecognition.ui.bluetooth.connect

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.core.app.NotificationCompat
import com.utexas.activityrecognition.R
import com.utexas.activityrecognition.ui.tcp.TcpClient
import kotlinx.coroutines.internal.SynchronizedObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

private const val TAG = "BluetoothService"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2

const val REQUEST_ENABLE_BT = 5

const val BT_NOTIF_ID = 1;
//Note to change some consts to an uncommitted file since we have a public repo
val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
const val UTHAR_DEVICE_NAME = "UTHAR_DEVICE"
lateinit var bluetoothAdapter: BluetoothAdapter
var isRunning = false
// ... (Add other message types here as needed.)

class MyBluetoothService : Service() {

    override fun onCreate() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        isRunning = true
        super.onCreate()
    }

    private fun getDevice(): BluetoothDevice? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            if (device.name == UTHAR_DEVICE_NAME){
                return device
            }
        }
        return null
    }


    private var socket: BluetoothSocket? = null
    private inner class ConnectBluetoothThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery()

            mmSocket?.connect()
            socket = mmSocket
        }
    }

    private var mTcpClient:TcpClient? = null;
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var builder = NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("UT Activity Recognition")
                .setContentText("Bluetooth Connected")
                .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(BT_NOTIF_ID, builder.build())
        val device: BluetoothDevice? = getDevice()
        var connectThread: ConnectBluetoothThread? = null
        device?.let {
            connectThread = ConnectBluetoothThread(it)
            connectThread!!.start()
        }
        connectThread?.join()
        val connectedThread: ConnectedBluetoothThread? = socket?.let { ConnectedBluetoothThread(it) }
        connectedThread?.start()
        val attachTCPThread: AttachTCPThread = AttachTCPThread()
        attachTCPThread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        try {
            socket?.close()
            mTcpClient?.stopClient()
            mTcpClient = null
            lock.withLock {
                condition.signalAll()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the connect socket", e)
        }
        isRunning = false
        super.onDestroy()
    }
    private inner class ConnectedBluetoothThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            while(true) {
                // Read from the InputStream.
                val jpegBitmap: ByteArray? = try {
                    getJpegBytes()
                } catch (e: IOException){
                    Log.d(TAG, "Input stream was disconnected", e)
                    stopSelf()
                    break
                }

                if(jpegBitmap == null){
                    continue
                }
                lock.withLock {
                    mTcpClient.let {
                        it?.sendImgBytes(jpegBitmap)
                    }
                    condition.await()
                }
                TimeUnit.MILLISECONDS.sleep(200)
            }
            stopSelf()
            Log.e(TAG, "Bluetooth Socket closed")
        }

        private fun getJpegBytes(): ByteArray? {

            val byteMessage: ByteArray = "RCV_READY".toByteArray(Charsets.US_ASCII)
            val byteArrLen: Int = byteMessage.size + 1
            val byteArrToSend: ByteArray = ByteArray(byteArrLen)
            for(i in byteMessage.indices){
                byteArrToSend[i] = byteMessage[i]
            }
            byteArrToSend[byteArrLen - 1] = 0
            try {
                mmOutStream.write(byteArrToSend)
            } catch (e: Exception) {
                e.message?.let { Log.e(TAG, it) }
                stopSelf()
            }
            var numBytes = mmInStream.read(mmBuffer)
            while (numBytes > 64){
                numBytes = mmInStream.read(mmBuffer)
            }
            val headerString = String(mmBuffer, Charset.forName("UTF8")).substring(0, numBytes)
            val size = getJpegLength(headerString)
            if(size == -1){
                return null
            }
            val jpegBytes = ArrayList<Byte>()
            while(jpegBytes.size < size){
                numBytes = mmInStream.read(mmBuffer)
                var addBytes = mmBuffer.filterIndexed { index, _ ->
                    index < numBytes
                }.asIterable()
                jpegBytes.addAll(addBytes)
            }
            return jpegBytes.toByteArray()
        }

        private fun getJpegLength(headerString: String): Int{
            val headerMessage = "Content-Length: "
            if(headerString.contains(headerMessage)){
                return try {
                    headerString.substring(headerMessage.length, headerString.length - 4).toInt()
                } catch (e: NumberFormatException){
                    -1
                }
            } else {
                return -1
            }
        }

        // Call this from the main activity to send data to the remote device.
//        fun write(bytes: ByteArray) {
//            try {
//                mmOutStream.write(bytes)
//            } catch (e: IOException) {
//                Log.e(TAG, "Error occurred when sending data", e)
//
//                // Send a failure message back to the activity.
//                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
//                val bundle = Bundle().apply {
//                    putString("toast", "Couldn't send data to the other device")
//                }
//                writeErrorMsg.data = bundle
//                handler.sendMessage(writeErrorMsg)
//                return
//            }
//
//            // Share the sent message with the UI activity.
//            val writtenMsg = handler.obtainMessage(
//                    MESSAGE_WRITE, -1, -1, bytes)
//            writtenMsg.sendToTarget()
//        }

    }

    private inner class AttachTCPThread: Thread() {
        override fun run() {
            mTcpClient = TcpClient(object : TcpClient.OnMessageReceived {
                override fun messageReceived(message: String?) {
                    if (message.equals("IMG_RECV")) {
                        lock.withLock {
                            condition.signalAll()
                        }
                    }
                }
            })
            mTcpClient.let { it?.run() }
            Log.e(TAG, "TCP Receiver closed")
            stopSelf()
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}