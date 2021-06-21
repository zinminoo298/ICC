package com.example.icc.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Model.ViewSummeryModel
import com.example.icc.R

class ViewSummeryAdapter(private var Dataset: ArrayList<ViewSummeryModel>, private val context: Context) :
    RecyclerView.Adapter<ViewSummeryAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtDate = itemView.findViewById<TextView>(R.id.view_date)
        val txtCust = itemView.findViewById<TextView>(R.id.view_cust)
        val txtBui = itemView.findViewById<TextView>(R.id.view_bui)
        val txtCorner = itemView.findViewById<TextView>(R.id.view_corner)
        val txtQty = itemView.findViewById<TextView>(R.id.view_qty)
        val txtPrice = itemView.findViewById<TextView>(R.id.view_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_summery_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtdate = holder.txtDate
        val txtcust = holder.txtCust
        val txtbui = holder.txtBui
        val txtcor = holder.txtCorner
        val txtqty = holder.txtQty
        val txtprc = holder.txtPrice

        txtdate.text = Dataset[position].date
        txtcust.text= Dataset[position].customer
        txtbui.text = Dataset[position].business_code
        txtcor.text = Dataset[position].corner
        txtqty.text = Dataset[position].qty
        txtprc.text = Dataset[position].price
    }
    override fun getItemCount() = Dataset.size
}