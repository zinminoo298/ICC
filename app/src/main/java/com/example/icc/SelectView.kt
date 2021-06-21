package com.example.icc

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.icc.Adapters.ViewShelfAdapter
import com.example.icc.DataBase.DatabaseHandler

class SelectView : AppCompatActivity() {

    companion object{
        lateinit var db:DatabaseHandler
        lateinit var spinnerDate: Spinner
        lateinit var spinnerCust: Spinner
        lateinit var spinnerPrd: Spinner
        lateinit var spinnerCor: Spinner
        lateinit var spinnerShf: Spinner
        lateinit var buttonSummery: Button
        lateinit var buttonSearch: Button
        var cust = ""
        var prod = ""
        var c = ""
        var s = ""
        var date = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_view)

        spinnerDate = findViewById(R.id.spinner_date)
        spinnerCust = findViewById(R.id.spinner_cust)
        spinnerPrd = findViewById(R.id.spinner_prod)
        spinnerCor = findViewById(R.id.spinner_corner)
        spinnerShf = findViewById(R.id.spinner_shelf)
        buttonSummery = findViewById(R.id.btn_summery)
        buttonSearch = findViewById(R.id.btn_search)

        db = DatabaseHandler(this)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DatabaseHandler.dateList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDate.adapter = arrayAdapter

        spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                db.getOthers(spinnerDate.selectedItem.toString())
                println(spinnerDate.selectedItem.toString())

                val arrayAdapter1: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.custList)
                arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCust.adapter = arrayAdapter1

                val arrayAdapter2: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.buiList)
                arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerPrd.adapter = arrayAdapter2

                val arrayAdapter3: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.cornerList)
                arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCor.adapter = arrayAdapter3

                val arrayAdapter4: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.shelfList)
                arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerShf.adapter = arrayAdapter4
            }
        }

        spinnerCust.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(spinnerCust.selectedItem.toString()=="Select Cust"){

                }
                else{
                    db.getCorner(spinnerCust.selectedItem.toString(),spinnerDate.selectedItem.toString())
                    println(spinnerDate.selectedItem.toString())

                    val arrayAdapter3: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.cornerList)
                    arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCor.adapter = arrayAdapter3

                    val arrayAdapter4: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.shelfList)
                    arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerShf.adapter = arrayAdapter4
                }
            }
        }

        spinnerCor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(spinnerCor.selectedItem.toString()=="Select Corner"){

                }
                else{
                    db.getShelf(spinnerCor.selectedItem.toString(),spinnerCust.selectedItem.toString(),spinnerDate.selectedItem.toString())
                    println(spinnerDate.selectedItem.toString())

                    val arrayAdapter4: ArrayAdapter<String> = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, DatabaseHandler.shelfList)
                    arrayAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerShf.adapter = arrayAdapter4
                }
            }
        }

        buttonSummery.setOnClickListener {
            db.loadSummery()
            val intent = Intent(this,StockSummery::class.java)
            startActivity(intent)
        }

        buttonSearch.setOnClickListener {
            if(spinnerDate.selectedItem.toString()== "Select Date"){
                Toast.makeText(this,"Please Select Date",Toast.LENGTH_SHORT).show()
            }
            else{
                object : AsyncTask<Int?, Int?, Int?>() {
                    internal lateinit var pgd: ProgressDialog
                    override fun onPreExecute() {
                        pgd = ProgressDialog(this@SelectView)
                        pgd.setMessage("Please Wait")
                        pgd.setTitle("Loading Data")
                        pgd.show()
                        pgd.setCancelable(false)

                        super.onPreExecute()

                    }

                    override fun onPostExecute(result: Int?) {
                        pgd.dismiss()
                        val intent = Intent(this@SelectView, ViewFilterSearch::class.java)
                        startActivity(intent)
                        super.onPostExecute(result)
                    }

                    override fun doInBackground(vararg params: Int?): Int? {
                        db.getSearchFilter(spinnerCust.selectedItem.toString(),
                            spinnerPrd.selectedItem.toString(),
                            spinnerCor.selectedItem.toString(),
                            spinnerDate.selectedItem.toString(),
                            spinnerShf.selectedItem.toString())

                        if(spinnerCust.selectedItem.toString() != "Select Cust"){
                            cust = spinnerCust.selectedItem.toString()
                        }
                        else{
                            cust = "ALL"
                        }

                        if(spinnerPrd.selectedItem.toString() != "Select Prd"){
                            prod = spinnerPrd.selectedItem.toString()
                        }
                        else{
                            prod = "ALL"
                        }

                        if(spinnerCor.selectedItem.toString() != "Select Corner"){
                            c = spinnerCor.selectedItem.toString()
                        }
                        else{
                            c = "ALL"
                        }

                        if(spinnerShf.selectedItem.toString() != "Select Shelf"){
                            s = spinnerShf.selectedItem.toString()
                        }
                        else{
                            s = "ALL"
                        }
                        date = spinnerDate.selectedItem.toString()
                        return null
                    }


                }.execute()
            }

    }
}
}