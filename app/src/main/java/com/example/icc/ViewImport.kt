package com.example.icc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.icc.Adapters.ViewModelAdapter
import com.example.icc.DataBase.DatabaseHandler

class ViewImport : AppCompatActivity() {
    companion object{
        lateinit var db: DatabaseHandler
        lateinit var buttonFile: ImageView
        lateinit var buttonImport: Button
        lateinit var buttonView: Button
        lateinit var editTextFile: EditText
        private lateinit var recyclerView: RecyclerView
        private lateinit var viewAdapter: RecyclerView.Adapter<*>
        private lateinit var viewManager: RecyclerView.LayoutManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_import)

        db=DatabaseHandler(this)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ViewModelAdapter(DatabaseHandler.ViewMaster, this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler1)
        recyclerView.apply {

            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
    }
}