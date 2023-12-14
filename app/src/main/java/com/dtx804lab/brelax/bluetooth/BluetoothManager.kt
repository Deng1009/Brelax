package com.dtx804lab.brelax.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock

object BluetoothManager {

    private const val TAG = "Scan Activity"
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    enum class ConnectStatus {
        CONNECT, DISCONNECT
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val bluetoothReceiver = object: BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_ON -> { /* TODO 藍牙開啓 */ }
                        BluetoothAdapter.STATE_OFF -> { /* TODO 藍牙關閉 */ }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.i(TAG, "開始掃描")
                    deviceList.clear()
                    addressSet.clear()
                    bluetoothAdapter?.run {
                        bondedDevices
                            .filter { it.name != null }
                            .forEach {
                                deviceList.add(it)
                                addressSet.add(it.address)
                            }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                /* TODO 掃描結束 */
                    Log.i(TAG, "掃描結束")
                }
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (it.name != null && !addressSet.contains(it.address)) {
                            deviceList.add(it)
                            addressSet.add(it.address)
                        }
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    when (intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)) {
                        BluetoothDevice.BOND_BONDING -> {
                            Log.i(TAG, "Device: ${device?.name} is bounding...")
                        }
                        BluetoothDevice.BOND_BONDED -> {
                            boundLock.lock()
                            boundCondition.signalAll()
                            boundLock.unlock()
                            Log.i(TAG, "Device: ${device?.name} bound successes")
                        }
                        BluetoothDevice.BOND_NONE -> {
                            Log.i(TAG, "Device: ${device?.name} not bound")
                        }
                    }
                }
                BluetoothDevice.ACTION_PAIRING_REQUEST -> { /* 自動配對(setPin)? */  }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    bluetoothStatus = ConnectStatus.CONNECT
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    bluetoothStatus = ConnectStatus.DISCONNECT
                }
            }
        }

    }

    var bluetoothStatus = ConnectStatus.DISCONNECT
        private set

    val deviceList = mutableStateListOf<BluetoothDevice>()
    private val addressSet = mutableSetOf<String>()

    var dataChannel: Channel<Float>? = null
        private set
    private var connectedDevice: BluetoothDevice? = null
    private var currentSocket: BluetoothSocket? = null

    private val boundLock = ReentrantLock(true)
    private val boundCondition = boundLock.newCondition()

    fun initialize(context: Context) {
        bluetoothAdapter =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        context.registerReceiver(bluetoothReceiver, IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)      //藍牙開關狀態
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)  //藍牙開始掃描
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) //藍牙掃描結束
            addAction(BluetoothDevice.ACTION_FOUND)               //發現設備
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)  //設備綁定狀態變化
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)       //連接上設備
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)    //連接斷開
            addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)     //收到配對請求
        })
    }

    fun close(context: Context) {
        disconnect()
        context.unregisterReceiver(bluetoothReceiver)
    }

    @SuppressLint("MissingPermission")
    fun scanDevice(enable: Boolean) {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth adapter can not initialize")
            return
        }
        if (enable == bluetoothAdapter!!.isDiscovering) return
        if (enable) bluetoothAdapter!!.startDiscovery()
        else bluetoothAdapter!!.cancelDiscovery()
    }

    @SuppressLint("MissingPermission")
    suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        if (bluetoothAdapter!!.isDiscovering) bluetoothAdapter!!.cancelDiscovery()
        if (bluetoothStatus == ConnectStatus.CONNECT) return@withContext false
        if (device.bondState == BluetoothDevice.BOND_NONE) {
            device.createBond()
            boundLock.lock()
            try {
                boundCondition.await()
            } finally {
                boundLock.unlock()
            }
        }
        var socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
        try {
            socket.connect()
            Log.i(TAG, "connect device")
        } catch (_: Exception) {
            try {
                socket = device.javaClass.getMethod("createRfcommSocket", Int::class.java)
                    .invoke(device, 1) as BluetoothSocket
                socket.connect()
                Log.i(TAG, "connect device - try 2")
            } catch (exp: Exception) {
                exp.printStackTrace()
                socket.close()
                Log.e(TAG, "connect device failed!")
                return@withContext false
            }
        }
        connectedDevice = device
        currentSocket = socket
        Log.i(TAG, "connect device successes!")
        return@withContext true
    }

    suspend fun createReadThread() = withContext(Dispatchers.IO) {
        val input = currentSocket!!.inputStream
        val buffer = ByteArray(4)
        dataChannel = Channel(8)
        Log.i(TAG, "start channel")
        GlobalScope.launch(Dispatchers.IO) {
            while (currentSocket!!.isConnected) {
                if (input.available() > 6) {
                    if (input.read() != 0xAA) continue
                    if (input.read() != 0xAA) continue
                    input.read(buffer)
                    val value = ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).int
                    Log.d(TAG, "$value")
                    dataChannel!!.send(value.toFloat())
                }
                delay(50)
                if (currentSocket == null) break
            }
            Log.i(TAG, "close socket")
            dataChannel?.close()
        }
    }

    fun disconnect() {
        dataChannel?.close()
        currentSocket?.close()
        dataChannel = null
        currentSocket = null
        connectedDevice = null
    }

}