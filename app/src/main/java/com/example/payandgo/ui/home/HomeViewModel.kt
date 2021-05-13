package com.example.payandgo.ui.home

import androidx.lifecycle.ViewModel
import com.example.payandgo.models.Route

class HomeViewModel : ViewModel() {
    var routes = mutableListOf<Route>()
    var idRoutes = mutableListOf<String>()
    var cars = mutableListOf<String>()
}