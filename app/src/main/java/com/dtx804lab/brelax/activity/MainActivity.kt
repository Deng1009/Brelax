package com.dtx804lab.brelax.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.dtx804lab.brelax.BrelaxApplication.Companion.DAIRY_PAGE
import com.dtx804lab.brelax.BrelaxApplication.Companion.GAME_PAGE
import com.dtx804lab.brelax.bluetooth.BluetoothLeService
import com.dtx804lab.brelax.ui.MainScreen
import com.dtx804lab.brelax.ui.RequiresPermissionsDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named

class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "Brelax"

        val MAIN = PageDestination("Main")
        val PERMISSION_CHECK = PageDestination("Permission")
    }

    private val diaryPage = get<PageDestination>(named(DAIRY_PAGE))
    private val gamePage = get<PageDestination>(named(GAME_PAGE))

    private val serviceCallback = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as BluetoothLeService.ServiceBinder).getService()
            gamePage.service = service
            Log.i(TAG, "BluetoothLE service is bound.")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            return
        }

    }

    private fun NavController.backHome() = this.goto(MAIN)

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun CheckPermission(navController: NavController) {
        packageManager.takeIf {
            !it.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        }?.also {
            Toast.makeText(this, "Not support Bluetooth", Toast.LENGTH_SHORT).show()
            finish()
        }
        val permissionList = mutableListOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(android.Manifest.permission.BLUETOOTH_SCAN)
            permissionList.add(android.Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (!rememberMultiplePermissionsState(permissionList).allPermissionsGranted) {
            navController goto PERMISSION_CHECK
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = MAIN.route) {
                composable(MAIN.route) {
                    MainScreen(
                        onGame = { navController goto gamePage },
                        onDiary = { navController goto diaryPage },
//                        onHistory = { navController goto HistoryEntry },
//                        onSurvey = { navController goto SurveyEntry }
                    )
                    CheckPermission(navController)
                }
                gamePage.addPage(this, navController) { navController.backHome() }
                diaryPage.addPage(this, navController) { navController.backHome() }
                dialog(
                    route = PERMISSION_CHECK.route,
                    dialogProperties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false,
                    )
                ) {
                    RequiresPermissionsDialog {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bindService(
            Intent(this, BluetoothLeService::class.java),
            serviceCallback, BIND_AUTO_CREATE
        )
    }

}