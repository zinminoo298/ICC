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
import com.example.icc.*
import com.example.icc.DataBase.DatabaseHandler
import com.example.icc.Model.ViewShelfModel
import java.util.ArrayList

class ViewShelfAdapter(
    private var Dataset: ArrayList<ViewShelfModel>,
    private val context: Context
) :
    RecyclerView.Adapter<ViewShelfAdapter.MyViewHolder>() {
    companion object {
        internal lateinit var buttonSave: Button
        internal lateinit var buttonCancel: Button
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var dialog: AlertDialog
        internal lateinit var editTextCust: EditText
        internal lateinit var editTextProd: EditText
        internal lateinit var editTextCorner: EditText
        private var progressBar1: ProgressBar? = null
        lateinit var db1: DatabaseHandler
        var s = ""
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtLocation = itemView.findViewById<TextView>(R.id.txtview_location)
        val txtQty = itemView.findViewById<TextView>(R.id.txtview_qty)
        val txtPrice = itemView.findViewById<TextView>(R.id.txtview_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_data_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtlc = holder.txtLocation
        val txtqty = holder.txtQty
        val txtprc = holder.txtPrice

        txtlc.text = Dataset[position].location
        txtqty.text = Dataset[position].qty
        txtprc.text = Dataset[position].price

        holder.view.setOnClickListener {
            object : AsyncTask<Int?, Int?, Int?>() {
                internal lateinit var pgd: ProgressDialog
                override fun onPreExecute() {
                    pgd = ProgressDialog(context)
                    pgd.setMessage("Please Wait")
                    pgd.setTitle("Loading Data")
                    pgd.show()
                    pgd.setCancelable(false)

                    super.onPreExecute()

                }

                override fun onPostExecute(result: Int?) {
                    pgd.dismiss()
                    s = Dataset[position].location.toString()
                    val intent = Intent(context, ViewSearch::class.java)
                    context.startActivity(intent)
                    super.onPostExecute(result)
                }

                override fun doInBackground(vararg params: Int?): Int? {
                    db1 = DatabaseHandler(context)
                    db1.getSearch(CheckStockSetup.cust,
                    CheckStockSetup.prod,
                    CheckStockSetup.c,
                    CheckStockSetup.date,
                    CheckStockSetup.s)
                    return null
                }


            }.execute()
        }

        println()

    }

    override fun getItemCount() = Dataset.size
}