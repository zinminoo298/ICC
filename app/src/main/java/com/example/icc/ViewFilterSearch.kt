package com.example.icc

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Adapters.ViewFilterSearchAdapter
import com.example.icc.Adapters.ViewSearchAdapter
import com.example.icc.Adapters.ViewShelfAdapter
import com.example.icc.DataBase.DatabaseHandler
import com.example.icc.Model.ViewFilterSearchModel
import com.example.icc.Model.ViewSearchModel

class ViewFilterSearch : AppCompatActivity() {
    companion object{
        lateinit var db: DatabaseHandler
        lateinit var searchView: SearchView
        lateinit var textViewID: TextView
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
        setContentView(R.layout.activity_view_filter_search)

        textViewID = findViewById(R.id.id)
        searchView = findViewById(R.id.search_view)
        textViewCust = findViewById(R.id.txt_cust)
        textViewProd = findViewById(R.id.txt_prod)
        textViewC = findViewById(R.id.txt_c)
        textViewS = findViewById(R.id.txt_s)
        textViewDate = findViewById(R.id.txt_date)

        textViewCust.text = SelectView.cust
        textViewProd.text = SelectView.prod
        textViewC.text = SelectView.c
        textViewS.text = SelectView.s
        textViewDate.text = SelectView.date

        searchView.queryHint = "Barcode"
        searchView.setFocusable(true)
        searchView.setIconifiedByDefault(false)
        searchView.requestFocusFromTouch()

        textViewID.text = "Total : ${DatabaseHandler.searchTotal} record(s)    |    Qty : ${DatabaseHandler.searchQty} \nPrice : ${DatabaseHandler.searchPrice}"

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewFilterSearchAdapter(DatabaseHandler.ViewFilter, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        var list = DatabaseHandler.ViewFilter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // collapse the view ?
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // search goes here !!
                var filteredList = ArrayList<ViewFilterSearchModel>()
                if (newText != "") {
                    for (item in list) {
                        if (item.barcode!!.toLowerCase().contains(newText.toLowerCase())) {
                            filteredList.add(item)
                        }
                    }
                    setupRecyclerView(filteredList)
                } else {
                    setupRecyclerView(list)
                }
                return false
            }
        })
    }

    private fun setupRecyclerView(list: ArrayList<ViewFilterSearchModel>) {
        viewAdapter = ViewFilterSearchAdapter(list, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}