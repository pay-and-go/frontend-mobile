package com.example.payandgo.models

import android.os.Parcel
import android.os.Parcelable

data class Route(
    val startCity: String,
    val arrivalCity: String,
    val date: String,
    val latStart: Double,
    var lngStart: Double,
    val latArrival: Double,
    var lngArrival: Double,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startCity)
        parcel.writeString(arrivalCity)
        parcel.writeString(date)
        parcel.writeDouble(latStart)
        parcel.writeDouble(lngStart)
        parcel.writeDouble(latArrival)
        parcel.writeDouble(lngArrival)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Route> {
        override fun createFromParcel(parcel: Parcel): Route {
            return Route(parcel)
        }

        override fun newArray(size: Int): Array<Route?> {
            return arrayOfNulls(size)
        }
    }
}
