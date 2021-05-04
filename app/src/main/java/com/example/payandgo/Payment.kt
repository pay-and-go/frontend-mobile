package com.example.payandgo

data class Payment(
    val idPago: Int,
    val fechaPago: String,
    val horaPago : String,
    val licence: String,
    val peaje: Int,
    val valor: Int,
)
