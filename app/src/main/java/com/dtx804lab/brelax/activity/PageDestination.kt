package com.dtx804lab.brelax.activity

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dtx804lab.brelax.bluetooth.BluetoothLeService

open class PageDestination(
    routeName: String,
    arguments: Map<String, NavType<out Any?>> = mapOf()
) {

    val route = arguments.keys.joinToString(prefix = routeName, separator = "") { "/{$it}" }
    val args = arguments.map { navArgument(it.key) { type = it.value } }

    internal lateinit var service: BluetoothLeService

    protected open fun NavGraphBuilder.addGraph(
        controller: NavController,
        onPrevious: () -> Unit
    ) {}

    fun addPage(
        navGraphBuilder: NavGraphBuilder,
        navController: NavController,
        onPrevious: () -> Unit
    ) {
        navGraphBuilder.addGraph(navController, onPrevious)
    }

}

infix fun NavController.goto(destination: PageDestination) {
    this.navigate(destination.route.split("/")[0])
}

fun NavController.goto(
    destination: PageDestination,
    vararg args: Any,
    options: NavOptionsBuilder.() -> Unit = {}
) {
    val routeName = destination.route.split("/")[0]
    val route = args.joinToString(prefix = routeName, separator = "") {
        if (it is List<*>) {
            it.joinToString(prefix = "/[", postfix = "]", separator = ",")
        } else "/$it"
    }
    this.navigate(route, options)
}

fun Bundle.getStringList(key: String): List<String>? {
    return this.getStringArray(key)?.get(0)?.run {
        if (this == "[]") return@run listOf()
        trim {
            when (it) {
                '[', ']' -> true
                else -> false
            }
        }.split(",")
    }
}