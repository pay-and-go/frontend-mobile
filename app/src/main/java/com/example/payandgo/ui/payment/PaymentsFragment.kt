package com.example.payandgo.ui.payment

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.*
import com.example.payandgo.databinding.PaymentsFragmentBinding
import com.example.payandgo.models.Payment
import com.example.payandgo.utils.InitApplication

class PaymentsFragment : Fragment() {

    private lateinit var viewModel: PaymentsViewModel
    lateinit var mRecycleView: RecyclerView
    lateinit var mAdapter: PaymentAdapter
    private lateinit var bindingPayment: PaymentsFragmentBinding
    private lateinit var ctx: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingPayment = PaymentsFragmentBinding.inflate(inflater,container,false)
        return bindingPayment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        initRecycler()
    }

    fun initRecycler(){
        var userId:Int = InitApplication.prefs.getId()
        try {
            val response = apolloClient.query(VehicleByIdUserQuery(userId)).enqueue(
                object : ApolloCall.Callback<VehicleByIdUserQuery.Data>() {
                    override fun onResponse(response: Response<VehicleByIdUserQuery.Data>) {
                        //println("respuestaaaa ${response?.data}")
                        val arrayCars = response?.data?.vehicleByIdUser

                        if (arrayCars != null) {
                            for (car in arrayCars) {
                                try {
                                    val response2 = apolloClient.query(PaymentsByPlacaQuery(car?.placa.toString())).enqueue(
                                        object : ApolloCall.Callback<PaymentsByPlacaQuery.Data>() {
                                            override fun onResponse(response: Response<PaymentsByPlacaQuery.Data>) {
                                                //println("respuestaaaa ${response?.data}")
                                                val arrayPayments = response?.data?.paymentsByPlaca

                                                if (arrayPayments != null) {
                                                    for (payment in arrayPayments) {
                                                        var paymentAux = Payment(
                                                            payment?.idPago!!,
                                                            payment?.fechaPago.toString(),
                                                            payment?.horaPago.toString(),
                                                            payment?.placa,
                                                            payment?.peaje,
                                                            payment?.valor.toInt())
                                                        viewModel.payments.add(paymentAux)
                                                    }
                                                }

                                            }

                                            override fun onFailure(e: ApolloException) {
                                                println("****Error apolloClient $e")
                                            }
                                        });

                                }catch (e: Exception) {
                                    println("*****Error $e")
                                }




                            }
                        }
                    }

                    override fun onFailure(e: ApolloException) {
                        println("****Error apolloClient $e")
                    }
                });

        }catch (e: Exception) {
            println("*****Error $e")
        }
        Handler().postDelayed({
            mAdapter = PaymentAdapter(viewModel.payments)
            mRecycleView = bindingPayment.rvMyPayments
            mRecycleView.setHasFixedSize(true)
            mRecycleView.layoutManager = LinearLayoutManager(ctx)
            mAdapter.PaymentAdapter(viewModel.payments, ctx )
            mRecycleView.adapter = mAdapter

        },1000)

    }

}