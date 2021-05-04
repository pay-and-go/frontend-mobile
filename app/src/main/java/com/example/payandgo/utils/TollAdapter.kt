package com.example.payandgo.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.payandgo.R
import com.example.payandgo.models.Toll

class TollAdapter(tolls: List<Toll>): RecyclerView.Adapter<TollAdapter.ViewHolder>() {

    var tolls: MutableList<Toll> = ArrayList()
    lateinit var context: Context

    fun TollAdapter(tolls : MutableList<Toll>, context: Context){
        this.tolls = tolls
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_tolls, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tolls.get(position)
        holder.bind(item, context)
    }

    override fun getItemCount(): Int {
        return tolls.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val TollName = view.findViewById(R.id.textTollName) as TextView
        val TollTerritory = view.findViewById(R.id.textTollTerritory) as TextView
        val TollPrice = view.findViewById(R.id.textTollPrice) as TextView

        fun bind(toll: Toll, context: Context){
            TollName.text = toll.name
            TollTerritory.text = toll.territory
            TollPrice.text = "$"+toll.price.toString()
        }
    }
}