package com.example.payandgo


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.type.License
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {

    var routes = mutableListOf<Route>()

    lateinit var editTextDestination: EditText
    lateinit var buttonVerPeajes: Button

    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        initRecycler(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        editTextDestination = findViewById(R.id.editTextDestination)
        editTextDestination.setOnClickListener { onClickEditTextDestination() }

        buttonVerPeajes = findViewById(R.id.buttonVerPeajes)
        buttonVerPeajes.setOnClickListener { onClickButtonVerPeajes() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // Add a marker in Sydney and move the camera
        map.setOnMyLocationClickListener(this)
        //createMarker(4.637103, -74.082823, "La Nacho")
        enableLocation()
        val coordinates: LatLng  = LatLng(3.637103, -74.082823)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates,8f))
    }

    private fun createMarker(lat: Double, lng: Double, label: String) {
        // Add a marker in Sydney and move the camera
        val coordinates = LatLng(lat, lng)
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title(label)
        map.addMarker(marker)
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){
            requestLocationPermission()
        }
    }

    private fun enableLocation() {
        if (!::map.isInitialized) return

        if (isLocationPermissionGranted()) {
            checkPermission()
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission()
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocationPermissionGranted()) {
            checkPermission()
            map.isMyLocationEnabled = false
            Toast.makeText(
                this,
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
        //createMarker(p0.latitude, p0.longitude, "Origen")
    }

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RouteAdapter

    fun initRecycler(ctx: Context) {
//        CoroutineScope(Dispatchers.IO).launch{
//            try {
//                val response = apolloClient.query(RouteByIdQuery(RouteIdInput("RT01"))).await()
//                println("respuesta ${response?.data?.routeById?.startCity}")
//
//            }catch (e: Exception){
//                println("*****Error $e")
//            }
//        }

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
            mRecyclerView = findViewById(R.id.rvRoouteListInMap) as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(ctx)
            mAdapter.RouteAdapter(routes as MutableList<Route>, ctx)
            mRecyclerView.adapter = mAdapter
        }, 1000)
    }

    private fun onClickEditTextDestination() {
        val i = Intent(this, RoutePlanningActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
    }

    private fun onClickButtonVerPeajes() {
        // Cambio Temporal ****** dejar el que está comentado
        val i = Intent(this, TollsActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
    }

}
