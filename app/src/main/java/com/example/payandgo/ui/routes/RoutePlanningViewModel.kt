package com.example.payandgo.ui.routes

import androidx.lifecycle.ViewModel
import com.example.payandgo.models.Route

class RoutePlanningViewModel : ViewModel() {
    var routes = mutableListOf<Route>()
    var idRoutes = mutableListOf<String>()
}