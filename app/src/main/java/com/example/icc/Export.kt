package com.example.icc

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Adapters.ViewExportAdapter
import com.example.icc.DataBase.DatabaseHandler
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Export : AppCompatActivity() {
    companion object{
        lateinit var db: DatabaseHandler
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
        lateinit var buttonTran: Button
        lateinit var buttonStk: Button
        lateinit var buttonSetting:Button
        var date=""
        var checkedBox = ""
        var device_name = ""
        var ip = ""


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)

        db = DatabaseHandler(this)
        buttonTran = findViewById(R.id.btn_tran)
        buttonStk = findViewById(R.id.btn_st)
        buttonSetting = findViewById(R.id.btn_setting)
        loadCheckedBox()
        loadDevice()
        loadIp()
        db.loadExport()

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewExportAdapter(DatabaseHandler.ViewExport, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        buttonTran.setOnClickListener {
            exportTransALL()
        }

        buttonStk.setOnClickListener {
            exportStkALL()
        }

        buttonSetting.setOnClickListener {
            val intent = Intent(this,Setting::class.java)
            startActivity(intent)
        }
    }

    fun exportStkALL(){
        getDateTime()
        val filepath1="/sdcard/Download/Export/${device_name}_${date}_PC01.txt"
        val filepath2="/sdcard/Download/Export/${device_name}_${date}_STK01.txt"
        val file1= File(filepath1)
        val file2= File(filepath2)
        if(file1.exists() && file2.exists())
        {
            AsyncExportStk(this).execute()
        }
        else{
            try {
                val root=File( "/sdcard/Download/Export")
                if (!root.exists()) {
                    root.mkdirs()
                }
                if(!file1.exists()) {
                    println(file1)
                    file1.createNewFile()
                }
                if(!file2.exists()) {
                    println(file2)
                    file2.createNewFile()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if(file1.exists() && file2.exists())
            {
                AsyncExportStk(this).execute()
            }

            else{
                Toast.makeText(this, "EXPORT UNSUCCESSFUL. MAKE SURE TO GIVE STORAGE ACCESS", Toast.LENGTH_LONG).show()
            }

        }
    }

    fun exportTransALL(){
        getDateTime()
        val filepath="/sdcard/Download/Export/${device_name}_${date}_TRANS_ALL.txt"
        val file= File(filepath)
        if(file.exists())
        {
            AsyncExportTran(this).execute()
        }
        else{
            generateNoteOnSD(this, "/${device_name}_${date}_TRANS_ALL.txt")
            if(file.exists())
            {
                AsyncExportTran(this).execute()
            }

            else{
                Toast.makeText(this, "EXPORT UNSUCCESSFUL. MAKE SURE TO GIVE STORAGE ACCESS", Toast.LENGTH_LONG).show()
            }

        }
    }

    fun generateNoteOnSD(context: Context, sFileName: String) {
        try {
            val root=File( "/sdcard/Download/Export")
            val file = File("/sdcard/Download/Export$sFileName")
            if (!root.exists()) {
                root.mkdirs()

            }
            if(!file.exists()) {
                println(file)
                file.createNewFile()
            }
        } catch (e: IOException) {
            e.printStackTrace()
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
        date = "$d$t"
        println(date)
    }

    private class AsyncExportTran(var context: Context): AsyncTask<String, String, String>() {


        internal lateinit var db: DatabaseHandler
        internal lateinit var pgd: ProgressDialog
        var resp:String? = null
        var state = ""
        var cancel:String? = null


        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context)
            resp ="Asyn Working"
            println(resp)
            try{
                exportTransAll()
                if(checkedBox == "ftp"){
                    FtpUpload("/sdcard/Download/Export/${device_name}_${date}_TRANS_ALL.txt","/${device_name}_${date}_TRANS_ALL.txt")
                }
            }
            catch(e:Exception){
                println(e)
                state = "false"
            }
            return resp!!
        }

        override fun onPostExecute(result: String?) {
            if(state == "false"){
                Toast.makeText(context,"Connection Error",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Export Successful",Toast.LENGTH_SHORT).show()
            }
            pgd.dismiss()
            super.onPostExecute(result)
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Exporting Data")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        fun FtpUpload(uploadFile:String,remoteFile:String) {
            var con: FTPClient? = null
            try {
                con = FTPClient()
                con.connectTimeout = 5000
                con.connect(ip)
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

        fun exportTransAll(){
            try {
                val db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
                val selectQuery=
                    " SELECT Customer,Business_Product,Product_Code,Qty,Price,(Price*Qty),Barcode,Date,Check_type,Hand_Code,Shelf,Corner,Category,Description,Reference From transaction_table ORDER BY Date ASC"
                val cursor=db.rawQuery(selectQuery, null)

                val saveFile=File("/sdcard/Download/Export/${device_name}_${date}_TRANS_ALL.txt")
                val fw=FileWriter(saveFile)
                var rowcount: Int
                var colcount: Int


                val bw= BufferedWriter(fw)
                bw.write("ST_COD\tBUSI_PROD\tPROD_CODE\tQTY\tPRICE\tS_PRICE\tBAR_CODE\tCHK_DAT\tCHK_TYPE\tHAND_CODE\tLOCATION\tCORNER\tCATEGORY PBILL\tDESCRIPTION\tREFERENCE\n")
                rowcount=cursor.getCount()
                colcount=cursor.getColumnCount()


                if (rowcount>0) {

                    for (i in 0 until rowcount) {
                        cursor!!.moveToPosition(i)

                        for (j in 0 until colcount) {
                            if (j == 0) {
                                bw.write(cursor!!.getString(j)+"\t")
                                println(cursor!!.getString(j))
                            }
                            if (j == 1) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 2) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 3) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 4) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 5) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 6) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 7) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 8) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 9) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 10) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 11) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 12) {
                                bw.write(cursor!!.getString(j)+"\t")
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
                }
            }
            catch (ex: Exception) {
                ex.printStackTrace()
                state = "false"

            }
        }

    }
    private class AsyncExportStk(var context: Context): AsyncTask<String, String, String>() {


        internal lateinit var db: DatabaseHandler
        internal lateinit var pgd: ProgressDialog
        var resp:String? = null
        var cancel:String? = null
        var state = ""

        override fun doInBackground(vararg params: String?): String {
            db = DatabaseHandler(context)
            resp ="Asyn Working"
            println(resp)
            try{
                exportStkAll()
                exportPCAll()
                if(checkedBox == "ftp"){
                    FtpUpload("/sdcard/Download/Export/${device_name}_${date}_STK01.txt","/${device_name}_${date}_STK01.txt")
                    FtpUpload("/sdcard/Download/Export/${device_name}_${date}_PC01.txt","/${device_name}_${date}_PC01.txt")
                }
            }
            catch(e:Exception){
                println(e)
                state = "false"
            }
            return resp!!
        }

        override fun onPostExecute(result: String?) {
            if(state == "false"){
                Toast.makeText(context,"Connection Error",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Export Successful",Toast.LENGTH_SHORT).show()
            }
            pgd.dismiss()
            super.onPostExecute(result)
        }

        override fun onPreExecute() {
            pgd = ProgressDialog(context)
            pgd.setMessage("Please Wait")
            pgd.setTitle("Exporting Data")
            pgd.show()
            pgd.setCancelable(false)

            super.onPreExecute()
        }

        fun FtpUpload(uploadFile:String,remoteFile:String) {
            var con: FTPClient? = null
            try {
                con = FTPClient()
                con.connectTimeout = 5000
                con.connect(ip)
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
                println(state)
                state = "false"
                e.printStackTrace()
            }
        }

        fun exportStkAll(){
            try {
                val db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
                val selectQuery=
                    " SELECT Business_Product,Barcode,Qty,Date,Customer,Shelf,(Price*Qty),Corner,Description FROM transaction_table ORDER BY Date ASC"
                val cursor=db.rawQuery(selectQuery, null)

                val saveFile=File("/sdcard/Download/Export/${device_name}_${date}_STK01.txt")
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
                                bw.write(cursor!!.getString(j)+"\t")
                                println(cursor!!.getString(j))
                            }
                            if (j == 1) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 2) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 3) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 4) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 5) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 6) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 7) {
                                bw.write(cursor!!.getString(j)+"\t")
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
            }
        }

        fun exportPCAll(){
            try {
                val db=context.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
                val selectQuery=
                    " SELECT Customer,Business_Product,SUM(Qty),Count(Barcode),Date,Corner FROM transaction_table GROUP BY  Date , Customer,Business_Product,Corner  ORDER BY Date ASC"
                val cursor=db.rawQuery(selectQuery, null)

                val saveFile=File("/sdcard/Download/Export/${device_name}_${date}_PC01.txt")
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
                                bw.write(cursor!!.getString(j)+"\t")
                                println(cursor!!.getString(j))
                            }
                            if (j == 1) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 2) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 3) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 4) {
                                bw.write(cursor!!.getString(j)+"\t")
                            }
                            if (j == 5) {
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
            }
        }

    }

    private fun loadDevice() {
        var prefs = getSharedPreferences("device", Activity.MODE_PRIVATE)
        device_name = prefs.getString("valdev", "").toString()
    }
    private fun loadIp() {
        var prefs = getSharedPreferences("ip", Activity.MODE_PRIVATE)
        ip = prefs.getString("valip", "").toString()
    }
    private fun loadCheckedBox() {
        var prefs = getSharedPreferences("box", Activity.MODE_PRIVATE)
        checkedBox = prefs.getString("valbox", "internal").toString()
    }
}