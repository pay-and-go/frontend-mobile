package com.example.payandgo.utils

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.coroutines.await
import com.example.payandgo.*
import com.example.payandgo.models.Route

class RouteAdapter(routes: List<Route>,) :RecyclerView.Adapter<RouteAdapter.ViewHolder>(){

    var routes: MutableList<Route> = ArrayList()
    var idRoutes: MutableList<String> = ArrayList()
    lateinit var context: Context

    fun RouteAdapter(routes : MutableList<Route>, idRoutes: MutableList<String>, context: Context){
        this.routes = routes
        this.idRoutes = idRoutes
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = routes.get(position)
        val idRoute = idRoutes.get(position)
        holder.bind(item, idRoute, context)
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
        fun bind(route: Route, idRoute: String, context: Context){
            startCity.text = route.startCity
            arrivalCity.text = route.arrivalCity
            description.text = route.date
            itemView.setOnClickListener(View.OnClickListener {

                val i = Intent(context, MyCarsActivity::class.java)
                i.putExtra("rutaSeleccionada", route)
                i.putExtra("IDrutaSeleccionada", idRoute)
                context.startActivity(i)
            })
        }
    }

    private fun sendDatatoMap(route: Route) {

    }
}
