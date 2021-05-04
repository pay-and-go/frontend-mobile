package com.example.payandgo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.payandgo.models.Payment

class PaymentAdapter(payment: List<Payment>): RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    var payments : MutableList<Payment> = ArrayList()
    lateinit var context: Context

    fun PaymentAdapter(payments: MutableList<Payment>, context: Context){
        this.payments = payments
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = payments.get(position)
        holder.bind(item, context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return  ViewHolder(layoutInflater.inflate(R.layout.item_payment, parent, false))
    }


    override fun getItemCount(): Int {
        return payments.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textNumberPayment = view.findViewById(R.id.textNumberPayment) as TextView
        val textDatePayment = view.findViewById(R.id.textDatePayment) as TextView
        val textLicencePayment = view.findViewById(R.id.textLicencePayment) as TextView
        val textTollId = view.findViewById(R.id.textTollId) as TextView
        val textValuePayment = view.findViewById(R.id.textValuePayment) as TextView

        fun bind(payment: Payment, context: Context){
            textNumberPayment.text = payment.idPago.toString()
            textDatePayment.text = payment.fechaPago
            textLicencePayment.text = payment.licence
            textTollId.text = payment.peaje.toString()
            textValuePayment.text = payment.valor.toString()
        }
    }

    private fun sendDatatoMap(payment: Payment) {
    }

}