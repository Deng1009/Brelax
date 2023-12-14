package com.dtx804lab.brelax.bluetooth

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothLeService : Service(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "Brelax BLE Service"
        val DEVICE_CONTENT: Map<UUID, List<UUID>> = mapOf(
            UUIDList.SIGNAL_SERVICE to listOf(
                UUIDList.SIGNAL_CHARACTERISTIC
            )
        )
    }

    private val binder = ServiceBinder()
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private val scannedAddressSet = mutableSetOf<String>()
    private val connectChannel = Channel<Boolean>()
    private val decoder = SignalDecoder()
    private var deviceGatt: BluetoothGatt? = null

    val deviceList = mutableStateListOf<BluetoothDevice>()
    val isScanning = mutableStateOf(false)
    val dataFlow = MutableSharedFlow<Signal>()

    /* Scan */

    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            with(result.device) {
                if (name == null || scannedAddressSet.contains(address)) return
                deviceList.add(this)
                scannedAddressSet.add(address)
            }
        }

    }

    fun scanDevice(enable: Boolean) {
        val scanner = bluetoothAdapter.bluetoothLeScanner ?: run {
            Log.w(TAG, "Bluetooth adapter can not initialize")
            return
        }
        isScanning.value = enable
        if (enable) {
            deviceList.clear()
            scannedAddressSet.clear()
            scanner.startScan(scanCallback)
        } else scanner.stopScan(scanCallback)
    }

    /* BLE */

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    decoder.init()
                    gatt.discoverServices()
                }

                BluetoothProfile.STATE_DISCONNECTED -> {

                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                connectChannel.trySend(false)
                Log.w(TAG, "onServicesDiscovered received: $status")
                return
            }
            if (!serviceCheck(gatt)) {
                connectChannel.trySend(false)
                Toast.makeText(
                    this@BluetoothLeService,
                    "裝置不匹配",
                    Toast.LENGTH_SHORT
                ).show()
                disconnect()
                return
            }
            connectChannel.trySend(true)
        }

        @Deprecated("Deprecated in Java")
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            when (characteristic.uuid) {
                UUIDList.SIGNAL_CHARACTERISTIC -> {
                    runBlocking {
                        decoder.signalDecode(characteristic.value).forEach {
                            dataFlow.emit(it)
                        }
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            when (characteristic.uuid) {
                UUIDList.SIGNAL_CHARACTERISTIC -> {
                    runBlocking {
                        decoder.signalDecode(value).forEach {
                            dataFlow.emit(it)
                        }
                    }
                }
            }
        }

    }

    private fun serviceCheck(gatt: BluetoothGatt): Boolean {
        gatt.services
            .map { it.uuid }
            .takeIf {
                it.containsAll(DEVICE_CONTENT.keys)
            }?.forEach { service ->
                if (!DEVICE_CONTENT.containsKey(service)) return@forEach
                gatt.getService(service).characteristics
                    .map { it.uuid }
                    .takeIf {
                        it.containsAll(DEVICE_CONTENT[service]!!)
                    } ?: return false
            } ?: return false
        return true
    }

    suspend fun connect(address: String): Boolean = withContext(Dispatchers.IO) {
        if (deviceGatt == null) {
            val device = bluetoothAdapter.getRemoteDevice(address) ?: return@withContext false
            deviceGatt =
                device.connectGatt(this@BluetoothLeService, false, gattCallback)
            return@withContext waitConnection()
        }
        return@withContext if (deviceGatt!!.device.address == address) {
            deviceGatt!!.connect()
            waitConnection()
        } else {
            disconnect()
            connect(address)
        }
    }

    fun disconnect() {
        if (deviceGatt == null) return
        deviceGatt!!.close()
        deviceGatt!!.disconnect()
        deviceGatt = null
    }

    fun signalNotify(enable: Boolean) {
        if (deviceGatt == null) return
        with(deviceGatt!!) {
            getService(UUIDList.SIGNAL_SERVICE)
                .getCharacteristic(UUIDList.SIGNAL_CHARACTERISTIC)
                .let { char ->
                    char.getDescriptor(UUIDList.CCC)
                        .let { desc ->
                            @Suppress("DEPRECATION")
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                desc.value =
                                    if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                    else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                                writeDescriptor(desc)
                            } else {
                                writeDescriptor(
                                    desc,
                                    if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                    else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                                )
                            }
                        }
                    this.setCharacteristicNotification(char, enable)
                }
        }
    }

    private suspend fun waitConnection(): Boolean = coroutineScope {
        val timeout = launch {
            delay(5000)
            connectChannel.trySend(false)
        }
        val result = connectChannel.receive()
        if (timeout.isActive) timeout.cancel(null)
        return@coroutineScope result
    }

    /* Override */

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
        connectChannel.close()
    }

    /* Service Binder */

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    inner class ServiceBinder : Binder() {

        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }

    }

}