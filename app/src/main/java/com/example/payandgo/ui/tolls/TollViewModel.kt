package com.example.payandgo.ui.tolls

import androidx.lifecycle.ViewModel
import com.example.payandgo.Toll

class TollViewModel : ViewModel() {
    var tolls = mutableListOf<Toll>()
}