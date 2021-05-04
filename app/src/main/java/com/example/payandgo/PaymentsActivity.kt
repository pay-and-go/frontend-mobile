package com.example.payandgo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.models.Payment
import com.example.payandgo.utils.InitApplication.Companion.prefs

class PaymentsActivity : AppCompatActivity() {

    var payments = mutableListOf<Payment>()
    lateinit var mRecycleView: RecyclerView
    lateinit var mAdapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)
        initRecycler()
    }

    fun initRecycler(){
        var userId:Int = prefs.getId()
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
                                                                payments.add(paymentAux)
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
            mAdapter = PaymentAdapter(payments)
            mRecycleView = findViewById(R.id.rvMyPayments) as RecyclerView
            mRecycleView.setHasFixedSize(true)
            mRecycleView.layoutManager = LinearLayoutManager(this)
            mAdapter.PaymentAdapter(payments as MutableList<Payment>, this)
            mRecycleView.adapter = mAdapter

        },1000)

    }

    fun verifyLicences(licences: List<String>){

    }
}