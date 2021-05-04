package com.example.payandgo

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.payandgo.models.Route
import com.example.payandgo.models.Toll
import com.example.payandgo.type.*
import com.example.payandgo.utils.TollAdapter
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PaymentSelectedRouteActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null

    var dayOfTodayDate: Int = 0
    var monthOfTodayDate: Int = 0
    var yearOfTodayDate: Int = 0
    private lateinit var route: Route
    private lateinit var licenseOfCar: String
    private lateinit var nuevoUltimoIdRuta: String

    var idNodoRoute: Int = 0
    var idNodoCar: Int = 0
    var idNodoDay: Int = 0


    var tolls = mutableListOf<Toll>()
    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: TollAdapter
    lateinit var buttonPay: Button
    var totalBalance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_selected_route)
        try {
            val objetoIntent: Intent = intent
            route = objetoIntent.getParcelableExtra("rutaSeleccionada")
            licenseOfCar = objetoIntent.getStringExtra("licenseOfCar")
            fillRouteInformation(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        initRecycler(this)

        buttonPay = findViewById(R.id.buttonPay)
        buttonPay.setOnClickListener { onClickButtonPay(this) }
    }

    private fun onClickButtonPay(ctx: Context) {
        agregarPago(ctx)
        agregarRelacionesRuta()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setIcon(R.mipmap.ic_launcher)
        progressDialog!!.setMessage("Cargando...")
        progressDialog!!.show()

        Handler().postDelayed({
            progressDialog!!.dismiss()
            Toast.makeText(ctx, "Pago Creado", Toast.LENGTH_SHORT).show()
            val i = Intent(this, InRouteActivity::class.java)
            val latLngSta = LatLng(route.latStart, route.lngStart)
            val latLngDes = LatLng(route.latArrival, route.lngArrival)
            i.putExtra("latLngOrigen", latLngSta)
            i.putExtra("latLngDestino", latLngDes)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 6000)
    }

    private fun agregarPago(ctx: Context) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.format(Date())

        val timeFormat = SimpleDateFormat("hh:mm:ss")
        val time = timeFormat.format(Date())

        for (toll in tolls) {
            val createPaymentMutation = CreatePaymentMutation(
                PaymentInput(
                    date,
                    time,
                    licenseOfCar,
                    toll.tollId,
                    toll.price.toDouble(),
                    date,
                    date
                )
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apolloClient.mutate(createPaymentMutation).await()
                    //Toast.makeText(ctx, "Pago Creado", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    println("exception $e")
                }
            }
        }
    }

    private fun obtenerUltimoIdRuta() {
        var ultimoId: String = ""

        try {
            val response = apolloClient.query(AllRoutesQuery()).enqueue(
                object : ApolloCall.Callback<AllRoutesQuery.Data>() {
                    override fun onResponse(response: Response<AllRoutesQuery.Data>) {
                        //println("respuestaaaaa ${response?.data}")
                        val arrayRoutes = response?.data?.allRoutes

                        if (arrayRoutes != null) {
                            ultimoId = (arrayRoutes.size).toString()
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
            //var numeroString: String = "" + ultimoId[2] + ultimoId[3]
            var numero: Int = ultimoId.toInt()
            numero += 1
            if (numero < 10) {
                nuevoUltimoIdRuta = "RT0$numero"
            } else {
                nuevoUltimoIdRuta = "RT$numero"
            }
        }, 2000)
    }

    private fun agregarRuta() {
        Handler().postDelayed({
            //println("********* $nuevoUltimoIdRuta")
            var description =
                "Viaje " + route.startCity.toString() + "-" + route.arrivalCity.toString()
            val createRouteMutation = CreateRouteMutation(
                RouteInput(
                    nuevoUltimoIdRuta,
                    Input.fromNullable(route.startCity.toString()),
                    Input.fromNullable(route.arrivalCity.toString()),
                    Input.fromNullable(description),
                    Input.fromNullable(route.latStart),
                    Input.fromNullable(route.lngStart),
                    Input.fromNullable(route.latArrival),
                    Input.fromNullable(route.lngArrival)
                )
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apolloClient.mutate(createRouteMutation).await()
                    idNodoRoute = response.data?.createRoute?.idNodeRoute?.toInt() ?: -1
                    //println("******** $idNodoRoute")

                } catch (e: Exception) {
                    println("exception $e")
                }
            }
        }, 2000)
    }

    private fun agregarRelacionesRuta() {
        agregarRuta()
        agregarDia()
        obtenerIdNodoCarro()

        Handler().postDelayed({
            crearRelaccionCarroDia()
            crearRelaccionDiaRuta()
        }, 5000)
    }

    private fun crearRelaccionDiaRuta() {
        val createRelaccionDiaRutaMutation = CreateRelationDayRouteMutation(
            DayRoute(idNodoRoute, idNodoDay)
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apolloClient.mutate(createRelaccionDiaRutaMutation).await()
                response.data?.createRelationDayRoute?.idRelationDateRoute?.toInt() ?: -1
                //println("******** $idNodoDay")
            } catch (e: Exception) {
                println("exception $e")
            }
        }
    }

    private fun crearRelaccionCarroDia() {
        val createRelaccionCarroDiaMutation = CreateRelationCarDayMutation(
            CarDay(idNodoCar, idNodoDay)
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apolloClient.mutate(createRelaccionCarroDiaMutation).await()
                response.data?.createRelationCarDay?.idRelationCarDate?.toInt() ?: -1
                //println("******** $idNodoDay")
            } catch (e: Exception) {
                println("exception $e")
            }
        }
    }

    private fun agregarDia() {
        val createDayMutation = CreateDayMutation(
            DateInput(
                dayOfTodayDate, monthOfTodayDate, yearOfTodayDate
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apolloClient.mutate(createDayMutation).await()
                idNodoDay = response.data?.createDay?.idNodeDate?.toInt() ?: -1
                println("******** $idNodoDay")
            } catch (e: Exception) {
                println("exception $e")
            }
        }
    }

    private fun obtenerIdNodoCarro() {
        try {
            val response = apolloClient.query(IdCarbyLicenseQuery(LicensecarInput(licenseOfCar))).enqueue(
                object : ApolloCall.Callback<IdCarbyLicenseQuery.Data>() {
                    override fun onResponse(response: Response<IdCarbyLicenseQuery.Data>) {
                        //println("respuestaaaaa ${response?.data}")
                        idNodoCar = response?.data?.idCarbyLicense?.idNodeCar?.toInt() ?: 0
                    }

                    override fun onFailure(e: ApolloException) {
                        println("****Error apolloClient $e")
                    }
                });
        } catch (e: Exception) {
            println("*****Error $e")
        }
    }

    private fun fillRouteInformation(view: PaymentSelectedRouteActivity) {
        val textStartCitySelected = view.findViewById(R.id.textStartCitySelected) as TextView
        val textArrivalCitySelected = view.findViewById(R.id.textArrivalCitySelected) as TextView
        val textTodayDate = view.findViewById(R.id.textTodayDate) as TextView
        textStartCitySelected.text = route.startCity
        textArrivalCitySelected.text = route.arrivalCity

        val day = SimpleDateFormat("dd")
        dayOfTodayDate = day.format(Date()).toInt()

        val month = SimpleDateFormat("M")
        monthOfTodayDate = month.format(Date()).toInt()

        val year = SimpleDateFormat("yyyy")
        yearOfTodayDate = year.format(Date()).toInt()

        textTodayDate.text =
            dayOfTodayDate.toString() + "/" + monthOfTodayDate.toString() + "/" + yearOfTodayDate.toString()
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
        for (toll in tolls) {
            sum += toll.price
        }
        totalBalance = sum
        return sum
    }

    fun initRecycler(view: PaymentSelectedRouteActivity) {
        try {

            val response = apolloClient.query(AllTollsQuery()).enqueue(
                object : ApolloCall.Callback<AllTollsQuery.Data>() {
                    override fun onResponse(response: Response<AllTollsQuery.Data>) {
                        //println("respuestaaaaa ${response?.data}")
                        val arrayTolls = response?.data?.allTolls

                        if (arrayTolls != null) {
                            var num1 = (0 until (arrayTolls?.size ?: 10)).random()
                            var num2 = (0 until (arrayTolls?.size ?: 10)).random()
                            while (num1 == num2) {
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

        //Se demora bastante - por eso se llama desde acá
        obtenerUltimoIdRuta()

        progressDialog = ProgressDialog(this)
        progressDialog!!.setIcon(R.mipmap.ic_launcher)
        progressDialog!!.setMessage("Cargando...")
        progressDialog!!.show()

        Handler().postDelayed({
            progressDialog!!.dismiss()
            val textTotalBalance = view.findViewById(R.id.textTotalBalance) as TextView
            textTotalBalance.text = "$" + getSumPricesTolls().toString()
            mAdapter = TollAdapter(tolls)
            mRecyclerView = findViewById(R.id.rvTollListSelected) as RecyclerView
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = LinearLayoutManager(this)
            mAdapter.TollAdapter(tolls as MutableList<Toll>, this)
            mRecyclerView.adapter = mAdapter
        }, 2000)

    }
}