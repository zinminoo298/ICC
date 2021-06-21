package com.example.icc

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Telephony.Carriers.SERVER
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.icc.DataBase.DatabaseHandler
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import java.net.InetAddress
import java.net.SocketException
import java.net.UnknownHostException


class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var db:DatabaseHandler
        lateinit var imageViewStock: ImageView
        lateinit var imageViewView: ImageView
        lateinit var imageViewEdit: ImageView
        lateinit var imageViewImport: ImageView
        lateinit var imageViewExport: ImageView
        lateinit var imageViewClear: ImageView
        lateinit var textViewQty: TextView
        lateinit var textViewItem: TextView
        lateinit var textViewAmount: TextView
        var STORAGE_PERMISSION_CODE = 1

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewStock = findViewById(R.id.img_stock)
        imageViewView = findViewById(R.id.img_view)
        imageViewEdit = findViewById(R.id.img_edit)
        imageViewImport = findViewById(R.id.img_import)
        imageViewExport = findViewById(R.id.img_export)
        imageViewClear = findViewById(R.id.img_clear)
        textViewAmount = findViewById(R.id.txtamount)
        textViewItem = findViewById(R.id.txtitem)
        textViewQty = findViewById(R.id.txtqty)

        db = DatabaseHandler(this)
        db.openDatabase()
        db.getMainSummery()

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestStoragePermission();
        }

        textViewQty.text = "Qty : "+DatabaseHandler.mainQty.toString()
        textViewItem.text ="Item : "+DatabaseHandler.mainItem.toString()
        textViewAmount.text = "$ : "+DatabaseHandler.mainAmount.toString()

        imageViewStock.setOnClickListener {
            val intent = Intent(this, CheckStockSetup::class.java)
            startActivity(intent)
        }

        imageViewView.setOnClickListener {
            object : AsyncTask<Int?, Int?, Int?>() {
                internal lateinit var pgd: ProgressDialog
                override fun onPreExecute() {
                    pgd = ProgressDialog(this@MainActivity)
                    pgd.setMessage("Please Wait")
                    pgd.setTitle("Loading Data")
                    pgd.show()
                    pgd.setCancelable(false)

                    super.onPreExecute()

                }

                override fun onPostExecute(result: Int?) {
                    pgd.dismiss()
                    if(DatabaseHandler.viewDataCheck==0){
                        Toast()
                    }
                    else{
                        val intent = Intent(this@MainActivity, SelectView::class.java)
                        startActivity(intent)
                    }

                    super.onPostExecute(result)
                }

                override fun doInBackground(vararg params: Int?): Int? {
                    db.getDate()
                    return null
                }


            }.execute()


        }

        imageViewExport.setOnClickListener {
            val intent = Intent(this, Export::class.java)
            startActivity(intent)
        }

        imageViewImport.setOnClickListener {
            val intent = Intent(this, Import::class.java)
            startActivity(intent)
//            object : AsyncTask<Int?, Int?, Int?>() {
//                internal lateinit var pgd: ProgressDialog
//                override fun onPreExecute() {
//                    pgd = ProgressDialog(this@MainActivity)
//                    pgd.setMessage("Please Wait")
//                    pgd.setTitle("Uploading Data")
//                    pgd.show()
//                    pgd.setCancelable(false)
//
//                    super.onPreExecute()
//
//                }
//
//                override fun onPostExecute(result: Int?) {
//                    pgd.dismiss()
//                    super.onPostExecute(result)
//                }
//
//                override fun doInBackground(vararg params: Int?): Int? {
//                    FtpUpload()
//                    return null
//                }
//
//
//            }.execute()

        }

        imageViewClear.setOnClickListener {
            if(textViewQty.text == "Qty : 0" && textViewItem.text =="Item : 0"&& textViewAmount.text == "$ : 0.00"){
                Toast.makeText(this, "No data to clear", Toast.LENGTH_SHORT).show()
            }
            else{
                alertDialog()
            }
        }

        imageViewEdit.setOnClickListener {
            object : AsyncTask<Int?, Int?, Int?>() {
                internal lateinit var pgd: ProgressDialog
                override fun onPreExecute() {
                    pgd = ProgressDialog(this@MainActivity)
                    pgd.setMessage("Please Wait")
                    pgd.setTitle("Loading Data")
                    pgd.show()
                    pgd.setCancelable(false)

                    super.onPreExecute()

                }

                override fun onPostExecute(result: Int?) {
                    pgd.dismiss()
                    if(DatabaseHandler.editKeyCheck== 0){
                        Toast()
                    }
                    else{
                        val intent = Intent(this@MainActivity, EditKey::class.java)
                        startActivity(intent)
                    }

                    super.onPostExecute(result)
                }

                override fun doInBackground(vararg params: Int?): Int? {
                    db.loadEditKey()
                    return null
                }


            }.execute()
        }
    }

    private fun Toast(){
        Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?")
        builder.setCancelable(false)

        builder.setPositiveButton(
                "Yes"
        ) { dialog, id ->
            val db = this.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
            db.execSQL("delete from transaction_table")
            db.execSQL("VACUUM")
            textViewQty.text = "Qty :0"
            textViewItem.text ="Item : 0"
            textViewAmount.text = "$ : 0.00"
        }

        builder.setNegativeButton(
                "No"
        ) { dialog, id -> dialog.cancel() }

        val alert: AlertDialog = builder.create()
        alert.show()
    }


    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )){
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }

        else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE
            )
        }
    }

    fun FtpUpload() {
        var con: FTPClient? = null
        try {
            con = FTPClient()
            con.connect("192.168.1.33")
            if (con.login("user1", "123456")) {
                con.enterLocalPassiveMode() // important!
                con.setFileType(FTP.BINARY_FILE_TYPE)
                val data = "/sdcard/Download/export.csv"
                val `in` = FileInputStream(File(data))
                val result: Boolean = con.storeFile("/export.csv", `in`)

                println("upload result")

                `in`.close()
                con.logout()
                con.disconnect()
            }
            else{
                println("NOPE")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    fun ft(){
//        val ftpClient = FTPClient()
//
//        try {
//            ftpClient.connect(InetAddress.getByName(SERVER))
//            ftpClient.login("user1", "123456")
//            if (ftpClient.replyString.contains("200")) {
//                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
//                var buffIn: BufferedInputStream? = null
//                buffIn = BufferedInputStream(FileInputStream("/sdcard/Download/export.csv"))
//                ftpClient.enterLocalPassiveMode()
//                val progressInput = ProgressInputStream(buffIn, progressHandler)
//                val result = ftpClient.storeFile(localAsset.getFileName(), progressInput)
//                buffIn.close()
//                ftpClient.logout()
//                ftpClient.disconnect()
//            }
//        } catch (e: SocketException) {
//            Log.e(SorensonApplication.TAG, e.getStackTrace().toString())
//        } catch (e: UnknownHostException) {
//            Log.e(SorensonApplication.TAG, e.getStackTrace().toString())
//        } catch (e: IOException) {
//            Log.e(SorensonApplication.TAG, e.stackTrace.toString())
//        }
//    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRestart() {
        db.getMainSummery()
        textViewQty.text = "Qty :"+DatabaseHandler.mainQty.toString()
        textViewItem.text ="Item : "+DatabaseHandler.mainItem.toString()
        textViewAmount.text = "$ : "+DatabaseHandler.mainAmount.toString()
        super.onRestart()
    }

}