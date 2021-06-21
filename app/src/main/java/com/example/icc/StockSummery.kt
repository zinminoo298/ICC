package com.example.icc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Adapters.ViewSummeryAdapter
import com.example.icc.DataBase.DatabaseHandler

class StockSummery : AppCompatActivity() {

    companion object{
        lateinit var db: DatabaseHandler
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
        private lateinit var textViewRec: TextView
        private lateinit var textViewQty: TextView
        private lateinit var textViewPrice: TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_summery)

        textViewRec = findViewById( R.id.total)
        textViewQty = findViewById( R.id.qty)
        textViewPrice = findViewById( R.id.price)
        db = DatabaseHandler(this)
        db.loadSummery()


        textViewRec.text = "Total Record : "+DatabaseHandler.totalRec
        textViewQty.text = "Total Qty : "+DatabaseHandler.totalQty
        textViewPrice.text ="Total Price : "+ DatabaseHandler.totalPrice

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewSummeryAdapter(DatabaseHandler.ViewSummery, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }

}