package com.dtx804lab.brelax.activity

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navigation
import com.dtx804lab.brelax.R
import com.dtx804lab.brelax.bluetooth.Signal
import com.dtx804lab.brelax.ui.GameScreen
import com.dtx804lab.brelax.ui.ScanDeviceScreen
import com.dtx804lab.brelax.ui.component.WaitingDialog
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GamePage : PageDestination("Game"), KoinComponent {

    companion object {

        private const val POS_THRESHOLD = 50
        private const val NEG_THRESHOLD = 50

        private val GAME = PageDestination("Game_Game")
        private val SCANNER = PageDestination("Game_Scanner")
        private val CONNECTING = PageDestination("Game_Connecting")

    }

    private suspend fun receiveData(
        flow: SharedFlow<Signal>,
        chestData: SnapshotStateList<Float>,
        chestBaseline: MutableFloatState,
        bellyData: SnapshotStateList<Float>,
        bellyBaseline: MutableFloatState,
        breathFactor: MutableFloatState
    ) {
        flow.collect { signal ->
            val chestValue = signal.s6.toFloat()
            val bellyValue = signal.s1.toFloat()

            chestData.add(chestValue)
            if (chestData.size > 250) chestData.removeAt(0)
            chestBaseline.floatValue = calculateBaseline(chestData)
            bellyData.add(bellyValue)
            if (bellyData.size > 250) bellyData.removeAt(0)
            bellyBaseline.floatValue = calculateBaseline(bellyData)

            breathFactor.floatValue = calculateFactor(chestData, bellyData)
        }
    }

    private fun calculateBaseline(list: List<Float>): Float {
        return list.min() * 0.9f + list.max() * 0.1f
    }

    private fun calculateFactor(chestData: List<Float>, bellyData: List<Float>): Float {
        if (chestData.size <= 20) return 0f
        val smooth = listOf(
            chestData.slice(0..4).average(),
            chestData.slice(5..9).average(),
            chestData.slice(10..14).average(),
            chestData.slice(15..19).average()
        )
        var result = 0.0
        for ((index, value) in smooth.withIndex()) {
            if (index == 0) continue
            result += value - smooth[index - 1]
        }
        return normalize((result / (smooth.size - 1)).toFloat())
    }

    private fun normalize(value: Float): Float {
        return if (value > 0) {
            val norm = value / POS_THRESHOLD
            if (norm > 1f) 1f else norm
        } else {
            val norm = value / NEG_THRESHOLD
            if (norm < -1f) -1f else norm
        }
    }

    override fun NavGraphBuilder.addGraph(
        controller: NavController,
        onPrevious: () -> Unit
    ) {
        navigation(
            route = this@GamePage.route,
            startDestination = SCANNER.route
        ) {
            composable(GAME.route) {
                val scope = rememberCoroutineScope()
                val chestData = remember { mutableStateListOf(0f, 1f, 2f, 3f) }
                val chestBaseline = remember { mutableFloatStateOf(0f) }
                val bellyData = remember { mutableStateListOf(3f, 2f, 1f, 0f) }
                val bellyBaseline = remember { mutableFloatStateOf(0f) }
                val breathFactor = remember { mutableFloatStateOf(0f) }
                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        service.signalNotify(true)
                        receiveData(
                            service.dataFlow,
                            chestData, chestBaseline,
                            bellyData, bellyBaseline,
                            breathFactor
                        )
                    }
                }
                GameScreen(chestData, chestBaseline, bellyData, bellyBaseline, breathFactor)
                BackHandler {
                    service.disconnect()
                    onPrevious()
                }
            }
            composable(SCANNER.route) {
                val scope = rememberCoroutineScope()
                val isScanning = remember { service.isScanning }
                LaunchedEffect(Unit) {
                    service.scanDevice(true)
                }
                ScanDeviceScreen(
                    deviceList = service.deviceList,
                    isScanning = isScanning,
                    onScan = { service.scanDevice(it) },
                    onConnect = {
                        service.scanDevice(false)
                        val connectResult: Deferred<Boolean> = scope.async {
                            service.connect(it.address)
                        }
                        scope.launch(Dispatchers.IO) {
                            if (connectResult.await()) {
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        controller.context,
                                        R.string.connect_success,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    controller.goto(GAME) {
                                        popUpTo(SCANNER.route)
                                    }
                                }
                            } else {
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        controller.context,
                                        R.string.connect_failed,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    controller.popBackStack()
                                }
                            }
                        }
                        controller goto CONNECTING
                    }
                )
                BackHandler {
                    service.scanDevice(false)
                    onPrevious()
                }
            }
            dialog(
                route = CONNECTING.route,
                dialogProperties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                )
            ) {
                WaitingDialog(
                    hint = stringResource(id = R.string.connecting)
                )
            }
        }
    }

}