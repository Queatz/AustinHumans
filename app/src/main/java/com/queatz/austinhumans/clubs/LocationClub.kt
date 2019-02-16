package com.queatz.austinhumans.clubs

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class LocationClub(private val on: On) : OnLifecycle {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun on() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(on<ContextClub>().context)
    }

    @SuppressLint("MissingPermission")
    fun get(callback: (location: Location) -> Unit) {
        on<PermissionClub>().obtain(Manifest.permission.ACCESS_FINE_LOCATION) {
            fusedLocationClient.requestLocationUpdates(
                    LocationRequest().setNumUpdates(1),
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            callback.invoke(locationResult.lastLocation)
                        }
                    },
                    Looper.getMainLooper()
            )
        }
    }

}
