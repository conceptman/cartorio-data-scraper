package com.example.cartorioapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartorioAdapter(private var list: List<Cartorio>) : RecyclerView.Adapter<CartorioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvCns: TextView = view.findViewById(R.id.tvCns)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cartorio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.name
        holder.tvCns.text = "CNS: ${item.cns}"
        holder.tvAddress.text = "${item.address} - ${item.state}"
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<Cartorio>) {
        list = newList
        notifyDataSetChanged()
    }
}
