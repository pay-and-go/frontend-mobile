package com.example.payandgo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RouteAdapter(routes: List<Route>) :RecyclerView.Adapter<RouteAdapter.ViewHolder>(){

    var routes: MutableList<Route> = ArrayList()
    lateinit var context: Context

    fun RouteAdapter(routes : MutableList<Route>, context: Context){
        this.routes = routes
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = routes.get(position)
        holder.bind(item, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_route, parent, false))
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val startCity = view.findViewById(R.id.textStartCity) as TextView
        val arrivalCity = view.findViewById(R.id.textArrivalCity) as TextView
        val description = view.findViewById(R.id.textDescription) as TextView
        fun bind(route:Route, context: Context){
            startCity.text = route.startCity
            arrivalCity.text = route.arrivalCity
            description.text = route.description
            itemView.setOnClickListener(View.OnClickListener { Toast.makeText(context, route.description, Toast.LENGTH_SHORT).show() })
        }
    }
}
