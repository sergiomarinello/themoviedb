package com.smarinello.themoviedb.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log

/**
 * Utils to evaluate the connectivity.
 */
class ConnectivityUtils(private val context: Context) {
    private val tag = this::class.java.simpleName

    /**
     * Gets if the internet is available on the device.
     */
    fun isInternetAccessAvailable(): Boolean {
        var isInternetConnectivityAvailable = false

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        connectivityManager?.let {
            val networks: Array<Network> = it.allNetworks
            for (network in networks) {
                val networkCapabilities: NetworkCapabilities? = it.getNetworkCapabilities(network)
                if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                    isInternetConnectivityAvailable = true
                }
            }
        }
        Log.d(tag, "isInternetConnectivityAvailable = $isInternetConnectivityAvailable")

        return isInternetConnectivityAvailable
    }
}
