package com.example.icc.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Model.ViewModel
import com.example.icc.R
import org.w3c.dom.Text

class ViewModelAdapter(private var Dataset: ArrayList<ViewModel>, private val context: Context) :
    RecyclerView.Adapter<ViewModelAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtBarcode = itemView.findViewById<TextView>(R.id.view_bc)
        val txtProductCode = itemView.findViewById<TextView>(R.id.view_pdcode)
        val txtDesc = itemView.findViewById<TextView>(R.id.view_desc)
        val txtPrice = itemView.findViewById<TextView>(R.id.view_price)
        val txtCategory = itemView.findViewById<TextView>(R.id.view_cat)
        val txtReference = itemView.findViewById<TextView>(R.id.view_ref)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_master_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtbc = holder.txtBarcode
        val txtprd = holder.txtProductCode
        val txtdesc = holder.txtDesc
        val txtprc = holder.txtPrice
        val txtcat = holder.txtCategory
        val txtref = holder.txtReference

        txtbc.text = Dataset[position].barcode
        txtprd.text= Dataset[position].productCode
        txtdesc.text = Dataset[position].description
        txtprc.text = Dataset[position].price
        txtcat.text = Dataset[position].category
        txtref.text = Dataset[position].reference
    }
    override fun getItemCount() = Dataset.size
}