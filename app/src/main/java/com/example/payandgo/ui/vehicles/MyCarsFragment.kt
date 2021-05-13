package com.example.payandgo.ui.vehicles

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.CarAdapter
import com.example.payandgo.R
import com.example.payandgo.VehicleByIdUserQuery
import com.example.payandgo.apolloClient
import com.example.payandgo.databinding.MyCarsFragmentBinding
import com.example.payandgo.models.Car
import com.example.payandgo.models.Route
import com.example.payandgo.ui.routes.RoutePlanningFragmentArgs
import com.example.payandgo.utils.InitApplication

class MyCarsFragment : Fragment() {

    lateinit var route: Route
    lateinit var idRoute: String
    lateinit var mRecycleView: RecyclerView
    lateinit var mAdapter: CarAdapter
    private lateinit var bindingCarsFragment: MyCarsFragmentBinding
    private lateinit var ctx: Context
    private val args: MyCarsFragmentArgs by navArgs()

    private lateinit var viewModel: MyCarsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCarsFragment = MyCarsFragmentBinding.inflate(inflater,container,false)
        route = args.safeRoute
        idRoute = "RT_01" //Preguntar Organista ***************************
        return bindingCarsFragment.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyCarsViewModel::class.java)
        initRecycler()
    }

    fun initRecycler(){
        var userId:Int = InitApplication.prefs.getId()
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

                                viewModel.cars.add(carAux)

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
            mAdapter = CarAdapter(viewModel.cars)
            mRecycleView = bindingCarsFragment.rvMyCars
            mRecycleView.setHasFixedSize(true)
            mRecycleView.layoutManager = LinearLayoutManager(ctx)
            mAdapter.CarAdapter(viewModel.cars,route,idRoute,  ctx)
            mRecycleView.adapter = mAdapter

        },1000)

    }

}