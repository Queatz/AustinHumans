package com.queatz.austinhumans

import android.Manifest
import android.util.Log
import com.queatz.austinhumans.clubs.*
import com.queatz.austinhumans.model.HumanModel
import com.queatz.on.On
import com.queatz.on.OnLifecycle

class MainActivityClub(private val on: On) : OnLifecycle {

    private lateinit var activity: MainActivity

    override fun on() {
        activity = on<ContextClub>().context as MainActivity
    }

    fun onViewCreated() {
        activity.showHumans(mutableListOf(
                HumanModel("Alice (You)", me = true),
                HumanModel("Amanda"),
                HumanModel("Esther"),
                HumanModel("Godwin"),
                HumanModel("Marshall"),
                HumanModel("Heather"),
                HumanModel("Amanda"),
                HumanModel("Esther"),
                HumanModel("Godwin"),
                HumanModel("Marshall"),
                HumanModel("Heather")))
    }

    fun onUseLocationClicked() {
        if (on<PermissionClub>().has(Manifest.permission.ACCESS_FINE_LOCATION)) {
            toggleSortByDistance()
        } else {
            on<AlertClub>().show(R.string.sort_by_distance, R.string.enable_location, R.string.nope) {
                toggleSortByDistance()
            }
        }
    }

    private fun toggleSortByDistance() {
        if (!on<StoreClub>().sortByDistance) {
            on<StoreClub>().sortByDistance = true
            on<LocationClub>().get {
                Log.w("LOCATION-FOUND", it.toString()) // TODO
            }
        } else {
            on<StoreClub>().sortByDistance = false
        }

        activity.sortByDistance(on<StoreClub>().sortByDistance)
    }

}