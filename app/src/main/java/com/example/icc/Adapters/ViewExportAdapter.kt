package com.example.icc.Adapters

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.DataBase.DatabaseHandler
import com.example.icc.EditKey
import com.example.icc.Export
import com.example.icc.MainActivity
import com.example.icc.Model.ViewExportModel
import com.example.icc.R
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ViewExportAdapter(
    private var Dataset: ArrayList<ViewExportModel>,
    private val context: Context
) :
    RecyclerView.Adapter<ViewExportAdapter.MyViewHolder>() {

    companion object{
        internal lateinit var buttonexportTran: Button
        internal lateinit var buttonexportStk: Button
        internal lateinit var buttonYes: Button
        internal lateinit var buttonNo: Button
        internal lateinit var dialog: AlertDialog
        private var progressBar1: ProgressBar? =null
        lateinit var db1:DatabaseHandler
        var state = ""
    }

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val txtDate = itemView.findViewById<TextView>(R.id.view_date)
        val txtCust = itemView.findViewById<TextView>(R.id.view_cust)
        val txtBui = itemView.findViewById<TextView>(R.id.view_bui)
        val txtCorner = itemView.findViewById<TextView>(R.id.view_corner)
        val txtQty = itemView.findViewById<TextView>(R.id.view_qty)
        val txtItem = itemView.findViewById<TextView>(R.id.view_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.view_export_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txtdate = holder.txtDate
        val txtcust = holder.txtCust
        val txtbui = holder.txtBui
        val txtcor = holder.txtCorner
        val txtqty = holder.txtQty
        val txtitem = holder.txtItem

        txtdate.text = Dataset[position].date
        txtcust.text= Dataset[position].customer
        txtbui.text = Dataset[position].business_code
        txtcor.text = Dataset[position].corner
        txtitem.text = Dataset[position].item
        txtqty.text = Dataset[position].qty

        holder.view.setOnLongClickListener(OnLongClickListener {
            alertDialog(
                Dataset[position].date!!,
                Dataset[position].customer!!,
                Dataset[position].business_code!!,
                Dataset[position].corner!!,
                position
            )
            true
        })
        holder.view.setOnClickListener {
            exportDialog(
                Dataset[position].date!!,
                Dataset[position].customer!!,
                Dataset[position].business_code!!,
                Dataset[position].corner!!
            )
        }

    }

    private fun alertDialog(date: String, cust: String, bui: String, corner: String,position: Int) {
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
            db.execSQL("delete from transaction_table WHERE Date='$date' AND Customer='$cust' AND Business_Product='$bui' AND Corner='$corner'")
//            db.execSQL("VACUUM")
//            db1.loadExport()
            progressBar1!!.visibility = View.GONE
            buttonYes.isEnabled = true
            buttonNo.isEnabled = true
            Dataset.removeAt(position)
//            recycler.removeViewAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, Dataset.size)
            dialog.dismiss()
        }

        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }


    private fun exportDialog(date: String, cust: String, bui: String, corner: String) {

        val builder = AlertDialog.Builder(context)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.export, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.setMessage("Select Export Option")
        progressBar1 = view.findViewById(R.id.progress_bar)
        progressBar1!!.visibility = View.GONE
        dialog.show()

        buttonexportTran = view.findViewById(R.id.btn_exportTran)
        buttonexportStk = view.findViewById(R.id.btn_exportStk)
        buttonexportTran.setOnClickListener {
            progressBar1!!.visibility = View.VISIBLE

            getDateTime()
            try {
                val root = File("/sdcard/Download/Export")
                if (!root.exists()) {
                    root.mkdirs()
                }
                exportTransN(date, cust, bui, corner)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        buttonexportStk.setOnClickListener {
            progressBar1!!.visibility = View.VISIBLE

            getDateTime()
            try {
                val root = File("/sdcard/Download/Export")
                if (!root.exists()) {
                    root.mkdirs()
                }
                exportStk01(date, cust, bui, corner)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    private fun getDateTime(){
        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH] + 1
        val year = c[Calendar.YEAR]
        val sdf = SimpleDateFormat("hhmmss")
        val dateformat = SimpleDateFormat("YYYYMMdd")
        val d = dateformat.format((Date()))
        val t = sdf.format(Date())
        Export.date = "$d$t"
        println(Export.date)
    }

    private fun exportTransN(date: String, cust: String, bui: String, corner: String) {

        try {
            val db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            val selectQuery=
                " SELECT Customer,Business_Product,Product_Code,Qty,Price,(Price*Qty),Barcode,Date,Check_type,Hand_Code,Shelf,Corner,Category,Description,Reference From transaction_table WHERE Date='$date'AND Customer='$cust' AND Business_Product = '$bui' AND Corner='$corner' ORDER BY Date ASC"
            val cursor=db.rawQuery(selectQuery, null)

            val saveFile=File("/sdcard/Download/Export/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_TRANS_N.txt")
            val fw=FileWriter(saveFile)
            var rowcount: Int
            var colcount: Int


            val bw= BufferedWriter(fw)
            bw.write("ST_COD\tBUSI_PROD\tPROD_CODE\tQTY\tPRICE\tS_PRICE\tBAR_CODE\tCHK_DAT\tCHK_TYPE\tHAND_CODE\tLOCATION\tCORNER\tCATEGORY PBILL\tDESCRIPTION\tREFERENCE\n")
            rowcount=cursor.getCount()
            colcount=cursor.getColumnCount()


            if (rowcount>0) {
                buttonexportTran.isEnabled = false
                buttonexportStk.isEnabled = false
                dialog.setCancelable(false)
                for (i in 0 until rowcount) {
                    cursor!!.moveToPosition(i)

                    for (j in 0 until colcount) {
                        if (j == 0) {
                            bw.write(cursor!!.getString(j) + "\t")
                            println(cursor!!.getString(j))
                        }
                        if (j == 1) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 2) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 3) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 4) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 5) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 6) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 7) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 8) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 9) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 10) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 11) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 12) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 13) {
                            bw.write("\t\t")
                        }
                        if (j == 14) {
                            bw.write(cursor!!.getString(j))
                        }
                    }
                    bw.newLine()
                }
                bw.flush()
                state = "true"
                if(Export.checkedBox == "ftp"){
                    object : AsyncTask<Int?, Int?, Int?>() {
                        internal lateinit var pgd: ProgressDialog
                        override fun onPreExecute() {
                            pgd = ProgressDialog(context)
                            pgd.setMessage("Please Wait")
                            pgd.setTitle("Exporting Data")
                            pgd.show()
                            pgd.setCancelable(false)

                            super.onPreExecute()

                        }

                        override fun onPostExecute(result: Int?) {
                            pgd.dismiss()
                            super.onPostExecute(result)
                        }

                        override fun doInBackground(vararg params: Int?): Int? {
                            FtpUpload("/sdcard/Download/Export/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_TRANS_N.txt","/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_TRANS_N.txt")
                            return null
                        }


                    }.execute()
                }
            }
            if(state == "false"){
                Toast.makeText(context,"Connection Error",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Export Successful",Toast.LENGTH_SHORT).show()
            }
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            state = "false"
            Toast.makeText(context, "Export Fail !", Toast.LENGTH_SHORT).show()
        }
        buttonexportTran.isEnabled = true
        buttonexportStk.isEnabled = true
        dialog.setCancelable(true)
        progressBar1!!.visibility = View.GONE

    }

    fun exportStk01(date: String, cust: String, bui: String, corner: String){
        try {
            buttonexportTran.isEnabled = false
            buttonexportStk.isEnabled = false
            dialog.setCancelable(false)
            val db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            val selectQuery=
                " SELECT Business_Product,Barcode,Qty,Date,Customer,Shelf,(Price*Qty),Corner,Description FROM transaction_table WHERE Date='$date'AND Customer='$cust' AND Business_Product = '$bui' AND Corner='$corner' ORDER BY Date ASC"
            val cursor=db.rawQuery(selectQuery, null)

            val saveFile=File("/sdcard/Download/Export/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_STK01.txt")
            val fw=FileWriter(saveFile)
            var rowcount: Int
            var colcount: Int


            val bw= BufferedWriter(fw)
            bw.write("BUSI_PROD\tBAR_CODE\tQTY\tDATE\tST_COD\tLOCATION\tPRICE\tCORNER\tDESCRIPTION\n")
            rowcount=cursor.getCount()
            colcount=cursor.getColumnCount()


            if (rowcount>0) {

                for (i in 0 until rowcount) {
                    cursor!!.moveToPosition(i)

                    for (j in 0 until colcount) {
                        if (j == 0) {
                            bw.write(cursor!!.getString(j) + "\t")
                            println(cursor!!.getString(j))
                        }
                        if (j == 1) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 2) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 3) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 4) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 5) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 6) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 7) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 8) {
                            bw.write(cursor!!.getString(j))
                        }
                    }
                    bw.newLine()
                }
                bw.flush()
                state = "true"

            }
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            state = "false"
            Toast.makeText(context, "Export Fail !", Toast.LENGTH_SHORT).show()
        }

        try {
            val db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            val selectQuery=
                " SELECT Customer,Business_Product,SUM(Qty),Count(Barcode),Date,Corner FROM transaction_table WHERE Date='$date'AND Customer='$cust' AND Business_Product = '$bui' AND Corner='$corner' GROUP BY  Date , Customer,Business_Product,Corner ORDER BY Date ASC"
            val cursor=db.rawQuery(selectQuery, null)

            val saveFile=File("/sdcard/Download/Export/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_PC01.txt")
            val fw=FileWriter(saveFile)
            var rowcount: Int
            var colcount: Int


            val bw= BufferedWriter(fw)
            bw.write("ST_COD\tBUSI_PROD\tTOTAL_QTY\tTOTAL_REC\tDATE\tCORNER\n")
            rowcount=cursor.getCount()
            colcount=cursor.getColumnCount()


            if (rowcount>0) {

                for (i in 0 until rowcount) {
                    cursor!!.moveToPosition(i)

                    for (j in 0 until colcount) {
                        if (j == 0) {
                            bw.write(cursor!!.getString(j) + "\t")
                            println(cursor!!.getString(j))
                        }
                        if (j == 1) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 2) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 3) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 4) {
                            bw.write(cursor!!.getString(j) + "\t")
                        }
                        if (j == 5) {
                            bw.write(cursor!!.getString(j))
                        }
                    }
                    bw.newLine()
                }
                bw.flush()
                state = "true"
                if(Export.checkedBox == "ftp"){
                    object : AsyncTask<Int?, Int?, Int?>() {
                        internal lateinit var pgd: ProgressDialog
                        override fun onPreExecute() {
                            pgd = ProgressDialog(context)
                            pgd.setMessage("Please Wait")
                            pgd.setTitle("Exporting Data")
                            pgd.show()
                            pgd.setCancelable(false)

                            super.onPreExecute()

                        }

                        override fun onPostExecute(result: Int?) {
                            pgd.dismiss()
                            super.onPostExecute(result)
                        }

                        override fun doInBackground(vararg params: Int?): Int? {
                            FtpUpload("/sdcard/Download/Export/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_STK01.txt","/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_STK01.txt")
                            FtpUpload("/sdcard/Download/Export/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_PC01.txt","/${Export.device_name}_${cust}_${bui}_${corner}_${Export.date}_PC01.txt")
                            return null
                        }


                    }.execute()

                }
            }
            if(state == "false"){
                Toast.makeText(context,"Connection Error",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Export Successful",Toast.LENGTH_SHORT).show()
            }
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            state = "false"
            Toast.makeText(context, "Export Fail !", Toast.LENGTH_SHORT).show()
        }

        buttonexportTran.isEnabled = true
        buttonexportStk.isEnabled = true
        dialog.setCancelable(true)
        progressBar1!!.visibility = View.GONE
    }

    fun FtpUpload(uploadFile:String,remoteFile:String) {
        var con: FTPClient? = null
        try {
            con = FTPClient()
            con.connectTimeout = 5000
            con.connect(Export.ip)
            if (con.login("user1", "123456")) {
                con.enterLocalPassiveMode() // important!
                con.setFileType(FTP.BINARY_FILE_TYPE)
                val data = uploadFile
                val `in` = FileInputStream(File(data))
                val result: Boolean = con.storeFile(remoteFile, `in`)

                println("upload result")

                `in`.close()
                con.logout()
                con.disconnect()
                state = "true"
            }
            else{
                state = "false"
                println("NOPE")
            }
        } catch (e: Exception) {
            state = "false"
            println(state)
            e.printStackTrace()
        }
    }

    override fun getItemCount() = Dataset.size

}