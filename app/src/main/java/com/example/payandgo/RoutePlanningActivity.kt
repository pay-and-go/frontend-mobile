package com.example.payandgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo.coroutines.await
import com.example.payandgo.models.Route
import com.example.payandgo.utils.RouteAdapter


class RoutePlanningActivity : AppCompatActivity() {

    var routes = mutableListOf<Route>()

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RouteAdapter
    lateinit var buttonPlanear: Button
    var arrayRoutes: List<AllRoutesQuery.AllRoute?>? = null

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

        //*********Por limitaciones, lo que queda comentado se omitirá
//        var addressListSta: List<Address>? = null
//        var addressListDes: List<Address>? = null

        if (locationSta == null || locationSta == "") {
            Toast.makeText(applicationContext,"escriba un origen",Toast.LENGTH_SHORT).show()
        }else{
            if (locationDes == null || locationDes == "") {
                Toast.makeText(applicationContext,"escriba un destino",Toast.LENGTH_SHORT).show()
            }
            else{
                //*********Por limitaciones, lo que queda comentado se omitirá
//                val geoCoder = Geocoder(this)
//                try {
//                    addressListSta = geoCoder.getFromLocationName(locationSta+", Colombia", 1)
//                    addressListDes = geoCoder.getFromLocationName(locationDes+", Colombia", 1)
//                    val addressSta = addressListSta!![0]
//                    val addressDes = addressListDes!![0]
//
//                    val i = Intent(this, MyCarsActivity::class.java)
//                    val route =  Route(locationSta, locationDes,
//                        "2/05/2021", addressSta.latitude, addressSta.longitude,
//                        addressDes.latitude, addressDes.longitude)
//                    i.putExtra("rutaSeleccionada", route)
//                    startActivity(i)
//
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }


                if(arrayRoutes == null){
                    try {
                        lifecycleScope.launchWhenResumed {
                            val response = apolloClient.query(AllRoutesQuery()).await()
                            arrayRoutes = response?.data?.allRoutes

                            checkIfExistRoute(locationDes)
                        }
                    } catch (e: Exception) {
                        println("*****Error $e")
                    }
                }else{
                    checkIfExistRoute(locationDes)
                }
            }
        }
    }

    fun checkIfExistRoute(locationDes: String){
        var found = false
        if (arrayRoutes != null) {
            for (route in arrayRoutes!!){
                if(route?.arrivalCity?.toLowerCase() == locationDes.toLowerCase()){
                    found = true

//                    val i = Intent(this, MyCarsActivity::class.java)
//                    val routeAux =  Route(route?.startCity, route?.arrivalCity,
//                        "2/05/2021", route?.latitudeStart!!.toDouble(), route?.longitudeStart!!.toDouble(),
//                        route?.latitudeEnd!!.toDouble(), route?.longitudeEnd!!.toDouble())
//                    i.putExtra("rutaSeleccionada", routeAux)
//                    i.putExtra("IDrutaSeleccionada", route.idRoute)
//                    startActivity(i)

                    //Borrar después y descomentar anterior
                    val routeAux =  Route(route?.startCity, route?.arrivalCity,
                        "2/05/2021", route?.latitudeStart!!.toDouble(), route?.longitudeStart!!.toDouble(),
                        route?.latitudeEnd!!.toDouble(), route?.longitudeEnd!!.toDouble())

                    val i = Intent(this, PaymentSelectedRouteActivity::class.java)
                    i.putExtra("rutaSeleccionada", routeAux)
                    i.putExtra("IDrutaSeleccionada", route?.idRoute)
                    i.putExtra("licenseOfCar", "AAA111")
                    startActivity(i)

                    //fin de borrar

                    break
                }
            }
            if(!found){
                Toast.makeText(applicationContext,"Por el momento este destino no está disponible",Toast.LENGTH_LONG).show()
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