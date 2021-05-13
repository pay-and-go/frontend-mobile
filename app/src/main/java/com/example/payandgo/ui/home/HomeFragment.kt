package com.example.payandgo.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.*
import com.example.payandgo.databinding.HomeFragmentBinding
import com.example.payandgo.models.Route
import com.example.payandgo.type.License
import com.example.payandgo.utils.InitApplication
import com.example.payandgo.utils.RouteAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment() {

    private lateinit var map: GoogleMap

    private lateinit var viewModel: HomeViewModel

    private lateinit var ctx: Context
    private lateinit var homeFragmentBinding: HomeFragmentBinding

    private lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RouteAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeFragmentBinding = HomeFragmentBinding.inflate(inflater, container, false)
        val rootView = homeFragmentBinding.root
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap -> map = googleMap
        }
        mRecyclerView = rootView.findViewById(R.id.rvRoouteListInMap) as RecyclerView
        homeFragmentBinding.editTextDestination.setOnClickListener {
            val safeArgs = ArrayList<Route>(viewModel.routes)
            val safeArgsIdRoutes = ArrayList<String>(viewModel.idRoutes)
            val action = HomeFragmentDirections.actionHomeFragmentToRoutePlanningFragment(safeArgs.toTypedArray(),safeArgsIdRoutes.toTypedArray())
            findNavController().navigate(action)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        initRecycler()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MapsActivity.REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission()
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    activity,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

//    override fun onResumeFragments() {
//        super.onResumeFragments()
//        if (!::map.isInitialized) return
//        if (!isLocationPermissionGranted()) {
//            checkPermission()
//            map.isMyLocationEnabled = false
//            Toast.makeText(
//                ctx,
//                "Para activar la localización ve a ajustes y acepta los permisos",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

//    override fun onMyLocationClick(p0: Location) {
//        Toast.makeText(ctx, "Estas en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
//        //createMarker(p0.latitude, p0.longitude, "Origen")
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        map = googleMap
//        // Add a marker in Sydney and move the camera
//        map.setOnMyLocationClickListener(this)
//        //createMarker(4.637103, -74.082823, "La Nacho")
//        enableLocation()
//        val coordinates: LatLng = LatLng(3.637103, -74.082823)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 8f))
//    }

    private fun createMarker(lat: Double, lng: Double, label: String) {
        // Add a marker in Sydney and move the camera
        val coordinates = LatLng(lat, lng)
        val marker: MarkerOptions = MarkerOptions().position(coordinates).title(label)
        map.addMarker(marker)
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinates))
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        ctx,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(){
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ctx,
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
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(ctx, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapsActivity.REQUEST_CODE_LOCATION
            )
        }
    }

    fun initRecycler() {
        checkAllCarsOfUser()
        //Para esperar la respuesta de la consulta anterior
        Handler().postDelayed({
            for (license in viewModel.cars) {
                try {
                    val response =
                        apolloClient.query(RoutesByLicenseQuery(License(license))).enqueue(
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
                                            viewModel.routes.add(rute)
                                            viewModel.idRoutes.add(routeAndDate?.route?.idRoute!!)
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

            }
            Handler().postDelayed({
                mAdapter = RouteAdapter(viewModel.routes)
                mRecyclerView = homeFragmentBinding.rvRoouteListInMap
                mRecyclerView.setHasFixedSize(true)
                mRecyclerView.layoutManager = LinearLayoutManager(ctx)
                mAdapter.RouteAdapter(viewModel.routes, viewModel.idRoutes, ctx)
                mRecyclerView.adapter = mAdapter
            }, 1500)

        }, 800)
    }

    fun checkAllCarsOfUser() {
        try {
            val response = apolloClient.query(VehicleByIdUserQuery(InitApplication.prefs.getId())).enqueue(
                object : ApolloCall.Callback<VehicleByIdUserQuery.Data>() {
                    override fun onResponse(response: Response<VehicleByIdUserQuery.Data>) {
                        val arrayCarsInformation = response?.data?.vehicleByIdUser

                        if (arrayCarsInformation != null) {
                            for (car in arrayCarsInformation) {
                                if (car != null) {
                                    viewModel.cars.add(car.placa)
                                }
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
    }

}