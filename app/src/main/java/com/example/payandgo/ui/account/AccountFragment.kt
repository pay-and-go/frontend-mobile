package com.example.payandgo.ui.account

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.payandgo.R
import com.example.payandgo.RoutePlanningActivity
import com.example.payandgo.databinding.FragmentAccountBinding
import com.example.payandgo.databinding.HomeFragmentBinding
import com.example.payandgo.ui.home.HomeFragmentDirections
import com.example.payandgo.utils.InitApplication.Companion.prefs


class AccountFragment : Fragment() {

    private lateinit var accountFragmentBinding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        accountFragmentBinding = FragmentAccountBinding.inflate(inflater,container,false)
        val rootView = accountFragmentBinding.root
        accountFragmentBinding.logout.setOnClickListener {
            prefs.wipe()
            val action = AccountFragmentDirections.actionAccountFragmentToLoginActivity()
            findNavController().navigate(action)
        }
        return rootView
    }
}