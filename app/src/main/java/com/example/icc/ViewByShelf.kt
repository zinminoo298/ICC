package com.example.icc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Adapters.ViewEditKeyAdapter
import com.example.icc.Adapters.ViewShelfAdapter
import com.example.icc.DataBase.DatabaseHandler

class ViewByShelf : AppCompatActivity() {

    companion object{
        lateinit var db: DatabaseHandler
        lateinit var textViewTotal: TextView
        lateinit var textViewCust: TextView
        lateinit var textViewProd: TextView
        lateinit var textViewC: TextView
        lateinit var textViewS: TextView
        lateinit var textViewDate: TextView
        private lateinit var textViewRec: TextView
        private lateinit var textViewQty: TextView
        private lateinit var textViewPrice: TextView
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_by_shelf)

        textViewCust = findViewById(R.id.txt_cust)
        textViewProd = findViewById(R.id.txt_prod)
        textViewC = findViewById(R.id.txt_c)
        textViewS = findViewById(R.id.txt_s)
        textViewDate = findViewById(R.id.txt_date)
        textViewRec = findViewById( R.id.total)
        textViewQty = findViewById( R.id.qty)
        textViewPrice = findViewById( R.id.price)

        textViewCust.text = CheckStockSetup.cust
        textViewProd.text = CheckStockSetup.prod
        textViewC.text = CheckStockSetup.c
        textViewS.text = CheckStockSetup.s
        textViewDate.text = CheckStockSetup.date
        textViewRec.text = "Total Record : "+DatabaseHandler.shelfTotal
        textViewQty.text = "Total Qty : "+DatabaseHandler.shelfQty
        textViewPrice.text ="Total Price : "+ DatabaseHandler.shelfPrice

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewShelfAdapter(DatabaseHandler.ViewShelf, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }
}
