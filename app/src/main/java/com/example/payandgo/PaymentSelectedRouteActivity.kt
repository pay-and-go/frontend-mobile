package com.example.payandgo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.type.PaymentInput
import com.example.payandgo.type.UserInput
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PaymentSelectedRouteActivity : AppCompatActivity() {

    var dayOfTodayDate: Int = 0
    var monthOfTodayDate: Int = 0
    var yearOfTodayDate: Int = 0
    private lateinit var route: Route
    private lateinit var licenseOfCar: String


    var tolls = mutableListOf<Toll>()
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: TollAdapter
    lateinit var buttonPay: Button
    var totalBalance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_selected_route)
        try{
            val objetoIntent: Intent =intent
            route = objetoIntent.getParcelableExtra("rutaSeleccionada")
            licenseOfCar = objetoIntent.getStringExtra("licenseOfCar")
            fillRouteInformation(this)
        }catch (e: Exception){
            e.printStackTrace()
        }
        initRecycler(this)

        buttonPay = findViewById(R.id.buttonPay)
        buttonPay.setOnClickListener { onClickButtonPay(this) }
    }

    private fun onClickButtonPay(ctx: Context) {
        agregarPago(ctx)
        agregarRuta()

        Toast.makeText(ctx, "Pago Creado", Toast.LENGTH_SHORT).show()
        val i = Intent(this, MapsActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
    }

    private fun agregarPago(ctx: Context) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.format(Date())

        val timeFormat = SimpleDateFormat("hh:mm:ss")
        val time = timeFormat.format(Date())

        for (toll in tolls){
            val createUserMutation = CreatePaymentMutation(PaymentInput(date,time,licenseOfCar,toll.tollId,toll.price.toDouble(),date,date))

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apolloClient.mutate(createUserMutation).await()
                    //Toast.makeText(ctx, "Pago Creado", Toast.LENGTH_SHORT).show()
                }  catch (e: Exception){
                    println("exception $e")
                }
            }
        }
    }

    private fun agregarRuta() {

    }

    private fun fillRouteInformation(view: PaymentSelectedRouteActivity) {
        val textStartCitySelected = view.findViewById(R.id.textStartCitySelected) as TextView
        val textArrivalCitySelected = view.findViewById(R.id.textArrivalCitySelected) as TextView
        val textTodayDate = view.findViewById(R.id.textTodayDate) as TextView
        textStartCitySelected.text = route.startCity
        textArrivalCitySelected.text = route.arrivalCity

        val day = SimpleDateFormat("dd")
        val dayOfTodayDate = day.format(Date())

        val month = SimpleDateFormat("M")
        val monthOfTodayDate = month.format(Date())

        val year = SimpleDateFormat("yyyy")
        val yearOfTodayDate = year.format(Date())

        textTodayDate.text = dayOfTodayDate.toString() + "/" + monthOfTodayDate.toString() + "/" + yearOfTodayDate.toString()
    }

    fun createToll(toll: AllTollsQuery.AllToll?): Toll {
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
        return tollAux
    }

    fun getSumPricesTolls(): Int {
        var sum = 0
        for(toll in tolls){
            sum += toll.price
        }
        totalBalance = sum
        return sum
    }

    fun initRecycler(view: PaymentSelectedRouteActivity){
        try {

            val response = apolloClient.query(AllTollsQuery()).enqueue(
                object : ApolloCall.Callback<AllTollsQuery.Data>() {
                    override fun onResponse(response: Response<AllTollsQuery.Data>) {
                        //println("respuestaaaaa ${response?.data}")
                        val arrayTolls = response?.data?.allTolls

                        if (arrayTolls != null) {
                            var num1 = (0 until (arrayTolls?.size ?: 10)).random()
                            var num2 = (0 until (arrayTolls?.size ?: 10)).random()
                            while (num1 == num2){
                                num2 = (0 until (arrayTolls?.size ?: 10)).random()
                            }

                            var toll1Data = arrayTolls?.get(num1)
                            var toll2Data = arrayTolls?.get(num2)

                            var toll1 = createToll(toll1Data)
                            var toll2 = createToll(toll2Data)

                            tolls.add(toll1)
                            tolls.add(toll2)
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
            val textTotalBalance = view.findViewById(R.id.textTotalBalance) as TextView
            textTotalBalance.text = "$" + getSumPricesTolls().toString()
            mAdapter = TollAdapter(tolls)
            mRecyclerView = findViewById(R.id.rvTollListSelected) as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mAdapter.TollAdapter(tolls as MutableList<Toll>, this)
            mRecyclerView.adapter = mAdapter
        }, 1500)

    }
}