package com.example.icc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Adapters.ViewEditKeyAdapter
import com.example.icc.Adapters.ViewExportAdapter
import com.example.icc.DataBase.DatabaseHandler

class EditKey : AppCompatActivity() {

    companion object{
        lateinit var db: DatabaseHandler
        lateinit var textViewTotal:TextView
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_key)

        db = DatabaseHandler(this)
        textViewTotal = findViewById(R.id.textTotal)
        textViewTotal.text = "Total Records : "+DatabaseHandler.editKeyCount.toString()

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewEditKeyAdapter(DatabaseHandler.ViewEdit, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }
}