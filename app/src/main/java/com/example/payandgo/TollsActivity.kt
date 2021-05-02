package com.example.payandgo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.type.License

class TollsActivity : AppCompatActivity() {

    var tolls = mutableListOf<Toll>()
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: TollAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tolls)
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
                                tolls.add(tollAux)
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
            mAdapter = TollAdapter(tolls)
            mRecyclerView = findViewById(R.id.rvTollList) as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mAdapter.TollAdapter(tolls as MutableList<Toll>, this)
            mRecyclerView.adapter = mAdapter
        }, 1000)

    }
}