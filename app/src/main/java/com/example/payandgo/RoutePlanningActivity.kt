package com.example.payandgo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class RoutePlanningActivity : AppCompatActivity() {

    val routes = listOf(
        Route("Bogotá", "Medellín", "Viaje Bog-Med"),
        Route("Bogotá", "Cali", "Viaje Bog-Cal"),
        Route("Bogotá", "Cartagena", "Viaje Bog-Car")
    )

    lateinit var mRecyclerView : RecyclerView
    val mAdapter : RouteAdapter = RouteAdapter(routes)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_planning)
        initRecycler()
    }

//    fun setUpRecyclerView(){
//        mRecyclerView = findViewById(R.id.rvSuperheroList) as RecyclerView
//        mRecyclerView.setHasFixedSize(true)
//        mRecyclerView.layoutManager = LinearLayoutManager(this)
//        mAdapter.RecyclerAdapter(getSuperheros(), this)
//        mRecyclerView.adapter = mAdapter
//    }

    fun initRecycler(){
        mRecyclerView = findViewById(R.id.rvRoouteList) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.RouteAdapter(routes as MutableList<Route>, this)
        mRecyclerView.adapter = mAdapter
    }
}