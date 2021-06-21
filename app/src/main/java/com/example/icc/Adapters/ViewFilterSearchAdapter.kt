package com.example.icc.Adapters

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.CheckStockSetup
import com.example.icc.DataBase.DatabaseHandler
import com.example.icc.Model.ViewFilterSearchModel
import com.example.icc.Model.ViewSearchModel
import com.example.icc.Model.ViewShelfModel
import com.example.icc.R
import com.example.icc.ViewImport
import java.util.ArrayList

class ViewFilterSearchAdapter(
    private var Dataset: ArrayList<ViewFilterSearchModel>,
    private val context: Context
) :
    RecyclerView.Adapter<ViewFilterSearchAdapter.MyViewHolder>() {

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtShelf = itemView.findViewById<TextView>(R.id.txtview_location)
        val txtBarcode = itemView.findViewById<TextView>(R.id.txtview_barcode)
        val txtQty = itemView.findViewById<TextView>(R.id.txtview_qty)
        val txtPrice = itemView.findViewById<TextView>(R.id.txtview_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_filter_search_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtSh = holder.txtShelf
        val txtbc = holder.txtBarcode
        val txtqty = holder.txtQty
        val txtprc = holder.txtPrice

        txtSh.text = Dataset[position].shelf
        txtbc.text = Dataset[position].barcode
        txtqty.text = Dataset[position].qty
        txtprc.text = Dataset[position].price

        println()

    }

    override fun getItemCount() = Dataset.size
}