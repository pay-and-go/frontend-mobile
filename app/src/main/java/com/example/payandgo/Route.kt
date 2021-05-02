package com.example.payandgo

data class Route(
    val startCity: String,
    val arrivalCity: String,
    val date: String,
    val latStart: Double,
    var lngStart: Double,
    val latArrival: Double,
    var lngArrival: Double,
)
