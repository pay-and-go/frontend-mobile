package com.example.payandgo.ui.vehicles

import androidx.lifecycle.ViewModel
import com.example.payandgo.models.Car

class MyCarsViewModel : ViewModel() {
    var cars = mutableListOf<Car>()
}