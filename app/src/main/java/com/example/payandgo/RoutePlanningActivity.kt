package com.example.payandgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.type.License
import android.location.Address
import android.location.Geocoder
import android.widget.Button
import android.widget.Toast
import java.io.IOException
import com.google.android.gms.maps.model.LatLng


class RoutePlanningActivity : AppCompatActivity() {

    var routes = mutableListOf<Route>()

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RouteAdapter
    lateinit var buttonPlanear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_planning)
        initRecycler()
        buttonPlanear = findViewById(R.id.buttonPlanear)
        buttonPlanear.setOnClickListener { onClickButtonPlanear() }
    }

    private fun onClickButtonPlanear() {
        val locationStart:EditText = findViewById<EditText>(R.id.editTextStart)
        val locationDestination:EditText = findViewById<EditText>(R.id.editTextDestination2)
        lateinit var locationSta: String
        lateinit var locationDes: String

        locationSta = locationStart.text.toString()
        locationDes = locationDestination.text.toString()

        var addressListSta: List<Address>? = null
        var addressListDes: List<Address>? = null

        if (locationSta == null || locationSta == "") {
            Toast.makeText(applicationContext,"escriba un origen",Toast.LENGTH_SHORT).show()
        }else{
            if (locationDes == null || locationDes == "") {
                Toast.makeText(applicationContext,"escriba un destino",Toast.LENGTH_SHORT).show()
            }
            else{
                val geoCoder = Geocoder(this)
                try {
                    addressListSta = geoCoder.getFromLocationName(locationSta, 1)
                    addressListDes = geoCoder.getFromLocationName(locationDes, 1)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val addressSta = addressListSta!![0]
                val addressDes = addressListDes!![0]
                val latLngSta = LatLng(addressSta.latitude, addressSta.longitude)
                val latLngDes = LatLng(addressDes.latitude, addressDes.longitude)
//                println("aaaaa $addressSta")
//                println("aaaaa ${addressSta.getAddressLine(0)}")
//                println("aaaaa ${addressDes.getAddressLine(0)}")
//                Toast.makeText(applicationContext, addressSta.latitude.toString() + " " + addressSta.longitude, Toast.LENGTH_LONG).show()
//                Toast.makeText(applicationContext, addressDes.latitude.toString() + " " + addressDes.longitude, Toast.LENGTH_LONG).show()

                val i = Intent(this, InRouteActivity::class.java)
                i.putExtra("latLngOrigen",latLngSta)
                i.putExtra("latLngDestino",latLngDes)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                finish()
            }
        }
    }

    fun initRecycler(){
        try {
            val response = apolloClient.query(RoutesByLicenseQuery(License("AAA111"))).enqueue(
                object : ApolloCall.Callback<RoutesByLicenseQuery.Data>() {
                    override fun onResponse(response: Response<RoutesByLicenseQuery.Data>) {
                        //println("respuesta ${response?.data?.getDatesByLicense?.get(0)}")
                        val arrayRoutesAndDates = response?.data?.getDatesByLicense

                        if (arrayRoutesAndDates != null) {
                            for (routeAndDate in arrayRoutesAndDates) {
                                var date =
                                    routeAndDate?.date?.dayTravel.toString() + "/" + routeAndDate?.date?.monthTravel.toString() + "/" + routeAndDate?.date?.yearTravel.toString()
                                var rute = Route(
                                    routeAndDate?.route?.startCity.toString(),
                                    routeAndDate?.route?.arrivalCity.toString(),
                                    date,
                                    routeAndDate?.route?.latitudeStart?.toDouble()!!,
                                    routeAndDate?.route?.longitudeStart?.toDouble()!!,
                                    routeAndDate?.route?.latitudeEnd?.toDouble()!!,
                                    routeAndDate?.route?.longitudeEnd?.toDouble()!!
                                )
                                routes.add(rute)
                            }
                        }
                    }
                    override fun onFailure(e: ApolloException) {
                        println("****Error apolloClient $e")
                    }
                });

        } catch (e: Exception) {
            println("*****Error $e")
        }
        Handler().postDelayed({
            mAdapter = RouteAdapter(routes)
            mRecyclerView = findViewById(R.id.rvRoouteList) as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mAdapter.RouteAdapter(routes as MutableList<Route>, this)
            mRecyclerView.adapter = mAdapter
        }, 1000)
    }
}