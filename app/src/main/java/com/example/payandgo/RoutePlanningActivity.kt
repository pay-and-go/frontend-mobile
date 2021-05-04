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

        try {
            val objetoIntent: Intent = intent
            routes = objetoIntent.getParcelableArrayListExtra<Route>("listaDeRutas")
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
                    addressListSta = geoCoder.getFromLocationName(locationSta+", Colombia", 1)
                    addressListDes = geoCoder.getFromLocationName(locationDes+", Colombia", 1)
                    val addressSta = addressListSta!![0]
                    val addressDes = addressListDes!![0]

                    val i = Intent(this, MyCarsActivity::class.java)
                    val route =  Route(locationSta, locationDes,
                        "2/05/2021", addressSta.latitude, addressSta.longitude,
                        addressDes.latitude, addressDes.longitude)
                    i.putExtra("rutaSeleccionada", route)
                    startActivity(i)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun initRecycler(){
        //Se hace la misma consulta en MapsActivity, por tanto recibe la lista de routes desde esa
        Handler().postDelayed({
            mAdapter = RouteAdapter(routes)
            mRecyclerView = findViewById(R.id.rvRoouteList) as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mAdapter.RouteAdapter(routes as MutableList<Route>, this)
            mRecyclerView.adapter = mAdapter
        }, 200)
    }
}