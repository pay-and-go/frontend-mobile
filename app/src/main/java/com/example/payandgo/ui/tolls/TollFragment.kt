package com.example.payandgo.ui.tolls

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.*
import com.example.payandgo.models.Toll
import com.example.payandgo.utils.TollAdapter

class TollFragment : Fragment() {

    companion object {
        fun newInstance() = TollFragment()
    }

    private lateinit var viewModel: TollViewModel
    private lateinit var ctx: Context
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: TollAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.toll_fragment, container, false)
        mRecyclerView = rootView.findViewById(R.id.rvTollList) as RecyclerView
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TollViewModel::class.java)
        initRecycler()
    }

    fun initRecycler(){

        try {
            val response = apolloClient.query(AllTollsQuery()).enqueue(
                object : ApolloCall.Callback<AllTollsQuery.Data>() {
                    override fun onResponse(response: Response<AllTollsQuery.Data>) {
                        //println("respuestaaaaa ${response?.data}")
                        val arrayTolls = response?.data?.allTolls

                        if (arrayTolls != null) {
                            for (toll in arrayTolls) {
                                var tollAux = Toll(
                                    toll?.tollId?.toInt()!!,
                                    toll?.name.toString(),
                                    toll?.territory.toString(),
                                    toll?.price?.toInt()!!,
                                    toll?.administrator.toString(),
                                    toll?.coor_lat?.toDouble()!!,
                                    toll?.coor_lng?.toDouble()!!,
                                    toll?.sector.toString(),
                                    toll?.crane_phone_number.toString(),
                                    toll?.toll_phone_number.toString()
                                )
                                viewModel.tolls.add(tollAux)
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
            mAdapter = TollAdapter(viewModel.tolls)
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(ctx)
            mAdapter.TollAdapter(viewModel.tolls as MutableList<Toll>, ctx)
            mRecyclerView.adapter = mAdapter
        }, 1500)

    }

}