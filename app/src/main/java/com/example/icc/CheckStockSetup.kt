package com.example.icc

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class CheckStockSetup : AppCompatActivity() {

    companion object{
        lateinit var radioButton1: RadioButton
        lateinit var radioButton2: RadioButton
        lateinit var textViewRadio: TextView
        lateinit var editTextHandCode: EditText
        lateinit var editTextCustomer: EditText
        lateinit var editTextProduct: EditText
        lateinit var editTextCorner: EditText
        lateinit var editTextShelf: EditText
        lateinit var editTextDate: TextView
        lateinit var buttonMenu: Button
        lateinit var buttonReset: Button
        lateinit var buttonNext: Button
        var h = ""
        var cust = ""
        var prod = ""
        var c = ""
        var s = ""
        var date = ""
        var sp_hc = ""
        var sp_cu = ""
        var sp_pr = ""
        var sp_cr = ""
        var sp_sh = ""
        var sp_cb = 0

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_stock_setup)

        radioButton1 = findViewById(R.id.radio1)
        radioButton2 = findViewById(R.id.radio2)
        textViewRadio = findViewById(R.id.radioSelected)
        editTextHandCode = findViewById(R.id.edit_handcode)
        editTextCustomer = findViewById(R.id.edit_customer)
        editTextProduct = findViewById(R.id.edit_product)
        editTextCorner = findViewById(R.id.edit_corner)
        editTextShelf = findViewById(R.id.edit_shelf)
        editTextDate = findViewById(R.id.edit_date)
        buttonMenu = findViewById(R.id.btn_menu)
        buttonReset = findViewById(R.id.btn_reset)
        buttonNext = findViewById(R.id.btn_next)

        loadHandCode()
        loadCheckBox()
        loadCorner()
        loadCust()
        loadProd()
        loadShelf()

        editTextHandCode.setText(sp_hc)
        editTextCustomer.setText(sp_cu)
        editTextProduct.setText(sp_pr)
        editTextCorner.setText(sp_cr)
        editTextShelf.setText(sp_sh)
        if(sp_cb == 1){
            radioButton1.isChecked = true
            radioButton2.isChecked = false
            textViewRadio.text = "1"
        }
        if(sp_cb == 2){
            radioButton2.isChecked = true
            radioButton1.isChecked = false
            textViewRadio.text = "2"
        }

        val dateF= SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val date1=dateF.format(Calendar.getInstance().time)
        editTextDate.setText(date1)

        radioButton1.setOnClickListener {
            radioButton2.isChecked = false
            textViewRadio.text = "1"
        }

        radioButton2.setOnClickListener {
            radioButton1.isChecked = false
            textViewRadio.text = "2"
        }

        buttonMenu.setOnClickListener {
            val myIntent = Intent(this, MainActivity::class.java)
            myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(myIntent)
        }

        buttonReset.setOnClickListener {
            loadHandCode()
            loadCheckBox()
            loadCorner()
            loadCust()
            loadProd()
            loadShelf()

            editTextHandCode.setText(sp_hc)
            editTextCustomer.setText(sp_cu)
            editTextProduct.setText(sp_pr)
            editTextCorner.setText(sp_cr)
            editTextShelf.setText(sp_sh)
            if(sp_cb == 1){
                radioButton1.isChecked = true
                radioButton2.isChecked = false
                textViewRadio.text = "1"
            }
            if(sp_cb == 2){
                radioButton2.isChecked = true
                radioButton1.isChecked = false
                textViewRadio.text = "2"
            }
        }

        buttonNext.setOnClickListener {
            h = editTextHandCode.text.toString()
            cust = editTextCustomer.text.toString()
            prod = editTextProduct.text.toString()
            c = editTextCorner.text.toString()
            date = editTextDate.text.toString()
            when{
                !radioButton1.isChecked && !radioButton2.isChecked -> Toast.makeText(this,"Please Select Front or Back",Toast.LENGTH_SHORT).show()
                editTextHandCode.text.toString()=="" -> Toast.makeText(this,"Plase enter Hand Code",Toast.LENGTH_SHORT).show()
                editTextCustomer.text.toString()=="" -> Toast.makeText(this,"Please enter customer",Toast.LENGTH_SHORT).show()
                editTextProduct.text.toString()=="" -> Toast.makeText(this,"Please enter product",Toast.LENGTH_SHORT).show()
                editTextCorner.text.toString()=="" -> Toast.makeText(this,"Please enter corner",Toast.LENGTH_SHORT).show()
                editTextShelf.text.toString()=="" -> Toast.makeText(this,"Please enter shelf",Toast.LENGTH_SHORT).show()
                editTextDate.text.toString()=="" -> Toast.makeText(this,"Please enter date",Toast.LENGTH_SHORT).show()
                else ->nextActivity()
            }
        }

        editTextDate.setOnClickListener {
            val c= Calendar.getInstance()
            var year=c.get(Calendar.YEAR)
            var month=c.get(Calendar.MONTH)
            var day=c.get(Calendar.DAY_OF_MONTH)

                val dpd=
                    DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, myear, mmonth, mday ->
                        day=mday
                        val realdate = "$mday".padStart(2,'0')
                        month=mmonth + 1
                        val realmonth = "$month".padStart(2,'0')
                        year=myear
                        editTextDate.setText("" + realdate + "/" + realmonth + "/" + year)
                    }, year, month, day)
                dpd.show()
        }
    }

    fun nextActivity(){
        setHandCode(editTextHandCode.text.toString())
        setCust(editTextCustomer.text.toString())
        setProd(editTextProduct.text.toString())
        setCorner(editTextCorner.text.toString())
        setShelf(editTextShelf.text.toString())
        setCheckBox(Integer.parseInt(textViewRadio.text.toString()))
        if(radioButton1.isChecked){
            s = "F${ editTextShelf.text.toString() }"
        }
        if(radioButton2.isChecked){
            s = "B${ editTextShelf.text.toString() }"
        }
        val intent = Intent(this,CountStock::class.java)
        startActivity(intent)
    }

    private fun setHandCode(v: String) {
        val editor = getSharedPreferences("hc", MODE_PRIVATE).edit()
        editor.putString("valhc", v)
        editor.apply()
    }
    private fun setCust(v: String) {
        val editor = getSharedPreferences("cu", MODE_PRIVATE).edit()
        editor.putString("valcu", v)
        editor.apply()
    }
    private fun setProd(v: String) {
        val editor = getSharedPreferences("pr", MODE_PRIVATE).edit()
        editor.putString("valpr", v)
        editor.apply()
    }
    private fun setCorner(v: String) {
        val editor = getSharedPreferences("cr", MODE_PRIVATE).edit()
        editor.putString("valcr", v)
        editor.apply()
    }
    private fun setShelf(v: String) {
        val editor = getSharedPreferences("sh", MODE_PRIVATE).edit()
        editor.putString("valsh", v)
        editor.apply()
    }
    private fun setCheckBox(v: Int) {
        val editor = getSharedPreferences("cb", MODE_PRIVATE).edit()
        editor.putInt("valcb", v)
        editor.apply()
    }

    private fun loadHandCode() {
        var prefs = getSharedPreferences("hc", Activity.MODE_PRIVATE)
        sp_hc = prefs.getString("valhc", "").toString()
    }
    private fun loadCust() {
        var prefs = getSharedPreferences("cu", Activity.MODE_PRIVATE)
        sp_cu = prefs.getString("valcu", "").toString()
    }
    private fun loadProd() {
        var prefs = getSharedPreferences("pr", Activity.MODE_PRIVATE)
        sp_pr = prefs.getString("valpr", "").toString()
    }
    private fun loadCorner() {
        var prefs = getSharedPreferences("cr", Activity.MODE_PRIVATE)
        sp_cr = prefs.getString("valcr", "").toString()
    }
    private fun loadShelf() {
        var prefs = getSharedPreferences("sh", Activity.MODE_PRIVATE)
        sp_sh = prefs.getString("valsh", "").toString()
    }
    private fun loadCheckBox() {
        var prefs = getSharedPreferences("cb", Activity.MODE_PRIVATE)
        sp_cb = prefs.getInt("valcb", 0)
    }

    override fun onBackPressed() {
        val myIntent = Intent(this, MainActivity::class.java)
        myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(myIntent)
        this.finish()
        super.onBackPressed()
    }
}