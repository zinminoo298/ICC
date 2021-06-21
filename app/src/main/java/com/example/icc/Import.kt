package com.example.icc

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.AsyncTask
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.icc.DataBase.DatabaseHandler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class Import : AppCompatActivity() {

    companion object {
        lateinit var db: DatabaseHandler
        lateinit var buttonFile: ImageView
        lateinit var buttonImport: Button
        lateinit var buttonView: Button
        lateinit var editTextFile: EditText
        lateinit var textViewRec: TextView
        lateinit var textViewCheckImport: TextView
        lateinit var checkBoxAppend: CheckBox
        var filepath = ""
        var importProgress = ""
        val requestcode = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import)

        db = DatabaseHandler(this)
        db.countMasterRec()

        buttonFile = findViewById(R.id.btn_file)
        buttonImport = findViewById(R.id.btn_import)
        buttonView = findViewById(R.id.btn_view)
        editTextFile = findViewById(R.id.edt_file)
        textViewRec = findViewById(R.id.totalRec)
        checkBoxAppend = findViewById(R.id.checkbox_append)
        textViewCheckImport = findViewById(R.id.importcheck)


        textViewRec.text ="Total Records : ${ DatabaseHandler.masterRec.toString() }"

        buttonFile.setOnClickListener {
            var fileintent = Intent(Intent.ACTION_GET_CONTENT)
            fileintent.type = "text/plain"
            fileintent.addCategory(Intent.CATEGORY_OPENABLE)
            fileintent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            try {
                startActivityForResult(fileintent, requestcode)
            } catch (e: ActivityNotFoundException) {
                editTextFile.setText("No activity can handle picking a file. Showing alternatives.")
            }
        }

        buttonImport.setOnClickListener {
            if(editTextFile.text.toString() =="")
            {
                Toast.makeText(this,"Please Select File",Toast.LENGTH_SHORT).show()
            }
            else{
                importProgress = "importing"
                Progress()
            }

        }

        buttonView.setOnClickListener {
            object : AsyncTask<Int?, Int?, Int?>() {
                internal lateinit var pgd: ProgressDialog
                override fun onPreExecute() {
                    pgd = ProgressDialog(this@Import)
                    pgd.setMessage("Please Wait")
                    pgd.setTitle("Loading Data")
                    pgd.show()
                    pgd.setCancelable(false)

                    super.onPreExecute()

                }

                override fun onPostExecute(result: Int?) {
                    pgd.dismiss()
                    if(DatabaseHandler.ViewMaster.size == 0){
                        textViewRec.text = "No master"
                    }
                    else{
                        val intent = Intent(this@Import,ViewImport::class.java)
                        startActivity(intent)
                    }

                    super.onPostExecute(result)
                }

                override fun doInBackground(vararg params: Int?): Int? {
                    db.loadMaster()
                    return null
                }


            }.execute()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null)
            return
        if (requestCode == requestcode) {
            filepath = data.data.toString()
            editTextFile.setText(data.data!!.path)
        }
    }

    private fun Progress(){
        val view = ProgressBar(this)
        view.setBackgroundColor(0xffffff)
        val windowParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
        windowParams.gravity = Gravity.CENTER
        windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        windowParams.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        windowParams.format = PixelFormat.TRANSLUCENT
        windowParams.windowAnimations = 0

        object : AsyncTask<Int?, Int?, Int?>() {
            override fun onPreExecute() {
                // init your dialog here;
                buttonImport.isEnabled = false
                buttonFile.isEnabled = false
                buttonView.isEnabled = false
                windowManager.addView(view, windowParams)
                textViewCheckImport.text="Importing"

            }

            override fun onPostExecute(result: Int?) {
                windowManager.removeView(view)
                buttonImport.isEnabled = true
                buttonFile.isEnabled = true
                buttonView.isEnabled = true
                textViewRec.text = "Total Records : ${ DatabaseHandler.masterRec.toString() }"
                if(importProgress == "complete"){
                    textViewCheckImport.text="Import Successful"
                }
                else{
                    textViewCheckImport.text="Import Fail!"
                }
                // process result;
            }

            override fun doInBackground(vararg params: Int?): Int? {
                // do your things here
                try {
                    importText()
                    db.countMasterRec()
//                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
                return null
            }


        }.execute()
        println(importProgress)
//        if(importProgress == "complete"){
//            textViewCheckImport.text="Import Successful"
//        }
//        else{
//            if(importProgress == "importing"){
//                textViewCheckImport.text="Importing"
//            }
//            else{
//                textViewCheckImport.text="Import Fail!"
//            }
//        }
    }

    private fun importText() {
        val cursor=contentResolver.openInputStream(android.net.Uri.parse(filepath))
        val db = this.openOrCreateDatabase("database.db", Context.MODE_PRIVATE, null)
        val tableName = "master"
        if(!checkBoxAppend.isChecked){
            db.execSQL("delete from $tableName")
        }
        else{
            //Do Nothing
        }
        db.execSQL("VACUUM")

        var linecount = 0

        try {
            try {
                val file=InputStreamReader(cursor)
                val buffer = BufferedReader(file)
                val contentValues = ContentValues()
                db.beginTransaction()
                val words: ArrayList<String> = ArrayList()
                var wordsArray: Array<String?>

                while (true) {
                    words.clear()
                    val line = buffer.readLine()
                    linecount++
                    println(linecount)

                    if (line == null) break

                    var business_product = ""
                    var product_code = ""
                    var barcode = ""
                    var category = ""
                    var price = ""
                    var s_price = ""
                    var desc = ""
                    var ref = ""
                    var total_array = ArrayList<String>()

                    wordsArray = line.split("\t").toTypedArray()
                    println("GGG" + wordsArray[6])
                    for (each in wordsArray) {
                        words.add(each!!.replace(" ", ""))
                    }
//                    for(each in words){
//                        println(each)
//                    }

                    business_product = words[0]
                    product_code = words[1]
                    barcode = words[2]
                    category = words[3]
                    price = words[4]
                    s_price = words[5]
                    desc = words[6]
                    ref = words[7]

                    total_array.add(business_product)
                    total_array.add(product_code)
                    total_array.add(barcode)
                    total_array.add(category)
                    total_array.add(price)
                    total_array.add(s_price)
                    total_array.add(desc)
                    total_array.add(ref)

                    contentValues.put("Business_Product", total_array[0])
                    contentValues.put("Product_Code", total_array[1])
                    contentValues.put("Barcode", total_array[2])
                    contentValues.put("Category", total_array[3])
                    contentValues.put("Price", total_array[4])
                    contentValues.put("S_Price", total_array[5])
                    contentValues.put("Description", total_array[6])
                    contentValues.put("Reference", total_array[7])

                    try{
                        db.insert(tableName, null, contentValues)
                    }
                    catch(e:IOException){

                    }
                }

                db.setTransactionSuccessful()
                db.endTransaction()
                importProgress = "complete"
            } catch (e: IOException) {
                if (db.inTransaction())
                    db.endTransaction()
                println("ERROR")
                importProgress = "fail"
            }

        } catch (e: IOException) {
            println(e)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                println("GG")
                if (importProgress == "importing") {
                    Toast.makeText(this, "Importing Master", Toast.LENGTH_SHORT).show()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onBackPressed() {
        when(importProgress){
            "importing" -> Toast.makeText(this, "Importing Master", Toast.LENGTH_SHORT).show()
            "complete", "error", "" -> super.onBackPressed()
        }
    }

}