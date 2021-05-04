package com.example.payandgo.ui.payment

import androidx.lifecycle.ViewModel
import com.example.payandgo.models.Payment

class PaymentsViewModel : ViewModel() {
    var payments = mutableListOf<Payment>()
}