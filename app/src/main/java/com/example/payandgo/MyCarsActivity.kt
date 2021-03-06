package com.example.payandgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.models.Car
import com.example.payandgo.models.Route
import com.example.payandgo.utils.InitApplication.Companion.prefs

class MyCarsActivity : AppCompatActivity() {

    var cars = mutableListOf<Car>()
    lateinit var route: Route
    lateinit var idRoute: String
    lateinit var mRecycleView: RecyclerView
    lateinit var mAdapter: CarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cars)
        initRecycler()

        try {
            val objetoIntent: Intent = intent
            route = objetoIntent.getParcelableExtra<Route>("rutaSeleccionada")
            idRoute = objetoIntent.getStringExtra("IDrutaSeleccionada")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initRecycler(){
        var userId:Int = prefs.getId()
        try {
            val response = apolloClient.query(VehicleByIdUserQuery(userId)).enqueue(
                    object : ApolloCall.Callback<VehicleByIdUserQuery.Data>() {
                        override fun onResponse(response: Response<VehicleByIdUserQuery.Data>) {
                            println("respuesta ${response?.data}")
                            val arrayCars = response?.data?.vehicleByIdUser

                            if (arrayCars != null) {
                                for (car in arrayCars) {
                                    var carAux = Car(
                                            car?.iduser!!.toInt(),
                                            car?.placa.toString(),
                                            car?.color.toString(),
                                            car?.tipo,
                                            car?.marca)

                                    cars.add(carAux)

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
            mAdapter = CarAdapter(cars)
            mRecycleView = findViewById(R.id.rvMyCars) as RecyclerView
            mRecycleView.setHasFixedSize(true)
            mRecycleView.layoutManager = LinearLayoutManager(this)
            //mAdapter.CarAdapter(cars as MutableList<Car>,route,  this)
            mAdapter.CarAdapter(cars as MutableList<Car>,route,idRoute,true,this)
            mRecycleView.adapter = mAdapter

        },1000)

    }

}