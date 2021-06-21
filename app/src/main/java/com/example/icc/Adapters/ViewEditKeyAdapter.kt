package com.example.icc.Adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.DataBase.DatabaseHandler
import com.example.icc.Model.ViewEditKeyModel
import com.example.icc.Model.ViewModel
import com.example.icc.R

class ViewEditKeyAdapter(private var Dataset: ArrayList<ViewEditKeyModel>, private val context: Context) :
    RecyclerView.Adapter<ViewEditKeyAdapter.MyViewHolder>() {

    companion object{
        internal lateinit var buttonSave: Button
        internal lateinit var buttonCancel: Button
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var dialog: AlertDialog
        internal lateinit var editTextCust: EditText
        internal lateinit var editTextProd: EditText
        internal lateinit var editTextCorner: EditText
        private var progressBar1: ProgressBar? =null
        lateinit var db1: DatabaseHandler
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtCust = itemView.findViewById<TextView>(R.id.txtview_cust)
        val txtProd = itemView.findViewById<TextView>(R.id.txtview_prod)
        val txtCorner = itemView.findViewById<TextView>(R.id.txtview_corner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_edit_key_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtcust = holder.txtCust
        val txtprd = holder.txtProd
        val txtcorner = holder.txtCorner

        txtcust.text = Dataset[position].cust
        txtprd.text= Dataset[position].prod
        txtcorner.text = Dataset[position].corner

        holder.view.setOnLongClickListener(View.OnLongClickListener {
            alertDialog(
                Dataset[position].cust!!,
                Dataset[position].prod!!,
                Dataset[position].corner!!,
                position
            )
            true
        })

        holder.view.setOnClickListener {
            editDialog(
                Dataset[position].cust!!,
                Dataset[position].prod!!,
                Dataset[position].corner!!,
                position
            )
        }
    }

    fun editDialog(cust:String, prod:String, corner:String,position: Int){
        val builder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.edit_key, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.setMessage("Select Export Option")
//        progressBar1 = view.findViewById(R.id.progress_bar)
//        progressBar1!!.visibility = View.GONE
        dialog.show()

        buttonSave = view.findViewById(R.id.btn_save)
        buttonCancel = view.findViewById(R.id.btn_canel)
        editTextCust = view.findViewById(R.id.edt_cust)
        editTextProd = view.findViewById(R.id.edt_prod)
        editTextCorner = view.findViewById(R.id.edt_corner)
        db1 = DatabaseHandler(context)
        editTextCust.setText(cust)
        editTextProd.setText(prod)
        editTextCorner.setText(corner)

        buttonSave.setOnClickListener {
            db1.updateKey(editTextCust.text.toString(), editTextProd.text.toString(), editTextCorner.text.toString(),cust,prod,corner)
            Dataset[position].cust = editTextCust.text.toString()
            Dataset[position].prod = editTextProd.text.toString()
            Dataset[position].corner = editTextCorner.text.toString()
            println("CHANGED"+Dataset[position].corner)
            notifyItemChanged(position)
            notifyItemRangeChanged(position, Dataset.size)
            dialog.dismiss()
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun alertDialog(cust: String, prod: String, corner: String,position: Int) {
        val builder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.delete, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.setMessage("Do you want to delete?")
        progressBar1 = view.findViewById(R.id.progress_bar)
        progressBar1!!.visibility = View.GONE
        dialog.show()
        dialog.setCancelable(false)

        buttonYes = view.findViewById(R.id.btn_yes)
        buttonNo = view.findViewById(R.id.btn_no)

        buttonYes.setOnClickListener {
            progressBar1!!.visibility = View.VISIBLE
            buttonYes.isEnabled = false
            buttonNo.isEnabled = false
            db1 = DatabaseHandler(context)
            val db = context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            db.execSQL("delete from transaction_table WHERE Customer='$cust' AND Business_Product='$prod' AND Corner='$corner'")
            buttonYes.isEnabled = true
            buttonNo.isEnabled = true
            Dataset.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, Dataset.size)
            progressBar1!!.visibility = View.GONE
            dialog.dismiss()
        }

        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun getItemCount() = Dataset.size
}