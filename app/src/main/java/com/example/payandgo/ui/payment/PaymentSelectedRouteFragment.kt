package com.example.payandgo.ui.payment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.payandgo.R

class PaymentSelectedRouteFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentSelectedRouteFragment()
    }

    private lateinit var viewModel: PaymentSelectedRouteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_selected_route_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentSelectedRouteViewModel::class.java)
        // TODO: Use the ViewModel
    }

}