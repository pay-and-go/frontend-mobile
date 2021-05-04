package com.example.payandgo.ui.routes

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.*
import com.example.payandgo.databinding.RoutePlanningFragmentBinding
import com.example.payandgo.type.License
import java.io.IOException

class RoutePlanningFragment : Fragment() {

    private lateinit var viewModel: RoutePlanningViewModel
    private lateinit var bindingRoutePlanningFragment: RoutePlanningFragmentBinding
    private lateinit var ctx: Context
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
        bindingRoutePlanningFragment = RoutePlanningFragmentBinding.inflate(inflater,container,false)
        val rootView = bindingRoutePlanningFragment.root
        bindingRoutePlanningFragment.buttonPlanear.setOnClickListener { onClickButtonPlanear() }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RoutePlanningViewModel::class.java)
        initRecycler()
    }

    private fun onClickButtonPlanear() {
        val locationStart: EditText = bindingRoutePlanningFragment.editTextStart
        val locationDestination: EditText = bindingRoutePlanningFragment.editTextDestination2
        lateinit var locationSta: String
        lateinit var locationDes: String

        locationSta = locationStart.text.toString()
        locationDes = locationDestination.text.toString()

        var addressListSta: List<Address>? = null
        var addressListDes: List<Address>? = null

        if (locationSta == null || locationSta == "") {
            Toast.makeText(ctx,"escriba un origen", Toast.LENGTH_SHORT).show()
        }else{
            if (locationDes == null || locationDes == "") {
                Toast.makeText(ctx,"escriba un destino", Toast.LENGTH_SHORT).show()
            }
            else{
                val geoCoder = Geocoder(ctx)
                try {
                    addressListSta = geoCoder.getFromLocationName(locationSta, 1)
                    addressListDes = geoCoder.getFromLocationName(locationDes, 1)
                    val addressSta = addressListSta!![0]
                    val addressDes = addressListDes!![0]

                    val licenseOfCar = "AAA111"

//                    val i = Intent(this, PaymentSelectedRouteActivity::class.java)
//                    val route =  Route(locationSta, locationDes,
//                        "2/05/2021", addressSta.latitude, addressSta.longitude,
//                        addressDes.latitude, addressDes.longitude)
//                    i.putExtra("rutaSeleccionada", route)
//                    i.putExtra("licenseOfCar", licenseOfCar)
//                    startActivity(i)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
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
                                viewModel.routes.add(rute)
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
            mAdapter = RouteAdapter(viewModel.routes)
            mRecyclerView = bindingRoutePlanningFragment.rvRoouteList
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(ctx)
            mAdapter.RouteAdapter(viewModel.routes, ctx)
            mRecyclerView.adapter = mAdapter
        }, 1000)
    }

}