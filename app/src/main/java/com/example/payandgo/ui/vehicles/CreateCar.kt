package com.example.payandgo.ui.vehicles

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo.coroutines.await
import com.example.payandgo.CreateCarMutation
import com.example.payandgo.CreateVehicleMutation
import com.example.payandgo.apolloClient
import com.example.payandgo.databinding.FragmentCreateCarBinding
import com.example.payandgo.type.CarInput
import com.example.payandgo.type.VehicleInput
import com.example.payandgo.ui.home.HomeFragmentDirections
import com.example.payandgo.utils.InitApplication.Companion.prefs
import kotlinx.coroutines.*


class CreateCar : Fragment() {

    private lateinit var bindingCreateCarFragment: FragmentCreateCarBinding

    private lateinit var ctx: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingCreateCarFragment = FragmentCreateCarBinding.inflate(inflater,container,false)

        bindingCreateCarFragment.btnCreate.setOnClickListener { GlobalScope.launch { createVehicle()  }}

        return bindingCreateCarFragment.root
    }


    suspend fun createVehicle(){
        val license = bindingCreateCarFragment.etLicence.text.toString()
        val brand = bindingCreateCarFragment.etBrand.text.toString()
        val color = bindingCreateCarFragment.etColor.text.toString()
        var create = false
        withContext(Dispatchers.IO){
            try {
                val response = apolloClient.mutate(CreateCarMutation(CarInput(prefs.getId().toString(), prefs.getFistName(),license))).await()
                println(response)
                val response2 = apolloClient.mutate(CreateVehicleMutation(VehicleInput(prefs.getId(),license,brand,1,color))).await()
                println("response2 ${response2}")
                create = true

            } catch (e: Exception) {
                println("exception $e")
            }
        }
        withContext(Dispatchers.Main){
            if(create){
                Toast.makeText(ctx, "Pago Creado", Toast.LENGTH_SHORT).show()
                val action = CreateCarDirections.actionCreateCarToHomeFragment()
                findNavController().navigate(action)
            }
        }

    }
}