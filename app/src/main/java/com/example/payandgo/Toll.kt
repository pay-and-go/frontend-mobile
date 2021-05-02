package com.example.payandgo

data class Toll (
    val tollId: Int,
    val name: String,
    val territory: String,
    val price: Int,
    val administrator: String,
    val coor_lat: Double,
    val coor_lng: Double,
    val sector: String,
    val crane_phone_number: String,
    val toll_phone_number: String
)