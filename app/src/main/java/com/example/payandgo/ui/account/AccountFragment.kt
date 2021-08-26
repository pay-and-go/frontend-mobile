package com.example.payandgo.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.apollographql.apollo.coroutines.await
import com.example.payandgo.*
import com.example.payandgo.databinding.FragmentAccountBinding
import com.example.payandgo.databinding.HomeFragmentBinding
import com.example.payandgo.type.CarInput
import com.example.payandgo.type.UserInputUpdate
import com.example.payandgo.type.VehicleInput
import com.example.payandgo.ui.home.HomeFragmentDirections
import com.example.payandgo.ui.vehicles.CreateCarDirections
import com.example.payandgo.utils.InitApplication.Companion.prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccountFragment : Fragment() {

    private lateinit var accountFragmentBinding: FragmentAccountBinding

    private lateinit var ctx: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountFragmentBinding = FragmentAccountBinding.inflate(inflater,container,false)
        val rootView = accountFragmentBinding.root

        accountFragmentBinding.btnUpdate.setOnClickListener {
            GlobalScope.launch { updateUser() }
        }


        accountFragmentBinding.logout.setOnClickListener {
            prefs.wipe()
            val action = AccountFragmentDirections.actionAccountFragmentToLoginActivity()
            findNavController().navigate(action)
        }

        accountFragmentBinding.etFirstName.setText(prefs.getFistName())
        accountFragmentBinding.etLastName.setText(prefs.getLastName())
        accountFragmentBinding.etIdentifier.setText(prefs.getCC().toString())
        accountFragmentBinding.etMail.setText(prefs.getMail())


        return rootView
    }

    suspend fun updateUser(){
        var update = false
        val firstName = accountFragmentBinding.etFirstName.text.toString()
        val lastName = accountFragmentBinding.etLastName.text.toString()
        val email = accountFragmentBinding.etMail.text.toString()
        val cc = Integer.valueOf(accountFragmentBinding.etIdentifier.text.toString())
        withContext(Dispatchers.IO){
            try {
                val response = apolloClient.mutate(UpdateUserMutation(
                    prefs.getMail(), UserInputUpdate(firstName,lastName,firstName,
                    cc,email), prefs.getToken())).await()
                println(response?.data?.updateUser)
                if(response?.data?.updateUser != null) {
                    if(response?.data?.updateUser?.success == "update successful"){
                        update = true
                    }
                }


            } catch (e: Exception) {
                println("exception $e")
            }
        }
        withContext(Dispatchers.Main){
            if(update){
                Toast.makeText(ctx, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                prefs.saveFirstName(firstName)
                prefs.saveLastName(lastName)
                prefs.saveCC(cc)
                prefs.saveMail(email)
            }
        }
    }
}