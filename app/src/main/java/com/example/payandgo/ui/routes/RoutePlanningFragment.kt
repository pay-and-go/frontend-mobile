package com.example.payandgo.ui.routes

import android.content.Context
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.payandgo.*
import com.example.payandgo.databinding.RoutePlanningFragmentBinding
import com.example.payandgo.models.Route
import com.example.payandgo.utils.RouteAdapter
import java.io.IOException

class RoutePlanningFragment : Fragment() {

    private lateinit var viewModel: RoutePlanningViewModel
    private lateinit var bindingRoutePlanningFragment: RoutePlanningFragmentBinding
    private lateinit var ctx: Context
    private lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: RouteAdapter
    private val args: RoutePlanningFragmentArgs by navArgs()

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
        viewModel.routes = args.argRoutes.toMutableList()
        initRecycler()
    }

    private fun onClickButtonPlanear() {
        val locationStart:EditText = bindingRoutePlanningFragment.editTextStart
        val locationDestination:EditText = bindingRoutePlanningFragment.editTextDestination2
        lateinit var locationSta: String
        lateinit var locationDes: String

        locationSta = locationStart.text.toString()
        locationDes = locationDestination.text.toString()

        var addressListSta: List<Address>? = null
        var addressListDes: List<Address>? = null

        if (locationSta == null || locationSta == "") {
            Toast.makeText(ctx,"escriba un origen",Toast.LENGTH_SHORT).show()
        }else{
            if (locationDes == null || locationDes == "") {
                Toast.makeText(ctx,"escriba un destino",Toast.LENGTH_SHORT).show()
            }
            else{
                val geoCoder = Geocoder(ctx)
                try {
                    addressListSta = geoCoder.getFromLocationName(locationSta+", Colombia", 1)
                    addressListDes = geoCoder.getFromLocationName(locationDes+", Colombia", 1)
                    val addressSta = addressListSta!![0]
                    val addressDes = addressListDes!![0]

                    val route =  Route(locationSta, locationDes,
                        "2/05/2021", addressSta.latitude, addressSta.longitude,
                        addressDes.latitude, addressDes.longitude)
                    val action = RoutePlanningFragmentDirections.actionRoutePlanningFragmentToMyCarsFragment(route)
                    findNavController().navigate(action)

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun initRecycler(){
        //Se hace la misma consulta en MapsActivity, por tanto recibe la lista de routes desde esa
        Handler().postDelayed({
            mAdapter = RouteAdapter(viewModel.routes)
            mRecyclerView = bindingRoutePlanningFragment.rvRoouteList
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(ctx)
            mAdapter.RouteAdapter(viewModel.routes, ctx)
            mRecyclerView.adapter = mAdapter
        }, 200)
    }

}