package com.example.payandgo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.payandgo.models.Car
import com.example.payandgo.models.Route

class CarAdapter(car: List<Car>): RecyclerView.Adapter<CarAdapter.ViewHolder>() {

    var cars : MutableList<Car> = ArrayList()
    var route: Route? = null
    var idRoute: String? = null
    var isPossibleClick: Boolean = false
    lateinit var context: Context

    fun CarAdapter(cars: MutableList<Car>, route: Route?, idRoute: String?,isPossibleClick: Boolean, context: Context){
        this.cars = cars
        this.route = route
        this.idRoute = idRoute
        this.isPossibleClick = isPossibleClick
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cars.get(position)
        holder.bind(item, this.route, this.idRoute, this.isPossibleClick, context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return  ViewHolder(layoutInflater.inflate(R.layout.item_car, parent, false))
    }


    override fun getItemCount(): Int {
        return cars.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val licence = view.findViewById(R.id.textLicence) as TextView
        val color = view.findViewById(R.id.textColor) as TextView
        val type = view.findViewById(R.id.textType) as TextView
        val brand = view.findViewById(R.id.textBrand) as TextView

        fun bind(car: Car, route: Route?, idRoute: String?, isPossibleClick: Boolean, context: Context){
            licence.text = car.licenseCar
            color.text = car.colorCar
            type.text = car.typeCar.toString()
            brand.text = car.brandCar
            if (isPossibleClick){
                itemView.setOnClickListener(View.OnClickListener {

                    val i = Intent(context, PaymentSelectedRouteActivity::class.java)
                    i.putExtra("rutaSeleccionada", route)
                    i.putExtra("IDrutaSeleccionada", idRoute)
                    i.putExtra("licenseOfCar", car.licenseCar)
                    context.startActivity(i)
                })
            }
        }
    }

    private fun sendDatatoMap(car: Car) {
    }

}


