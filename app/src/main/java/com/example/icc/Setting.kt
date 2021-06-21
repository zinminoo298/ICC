package com.example.icc

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class Setting : AppCompatActivity() {
    companion object{
        lateinit var checkBoxFtp: RadioButton
        lateinit var checkBoxInternal: RadioButton
        lateinit var editTextDevice: EditText
        lateinit var editTextIp: EditText
        lateinit var buttonSave: Button
        var checkedBox = ""
        var device_name = ""
        var ip = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        loadCheckedBox()
        loadDevice()
        loadIp()

        checkBoxFtp = findViewById(R.id.checkbox_ftp)
        checkBoxInternal = findViewById(R.id.checkbox_internal)
        editTextIp = findViewById(R.id.editText_ip)
        editTextDevice = findViewById(R.id.edt_device)
        buttonSave = findViewById(R.id.btn_save)

        editTextDevice.setText(device_name)
        editTextIp.setText(ip)
        if(checkedBox == "ftp"){
            checkBoxFtp.isChecked = true
        }
        if(checkedBox == "internal"){
            checkBoxInternal.isChecked = true
        }

        checkBoxFtp.setOnClickListener {
            checkBoxInternal.isChecked = false
            checkedBox = "ftp"
        }
        checkBoxInternal.setOnClickListener{
            checkBoxFtp.isChecked = false
            checkedBox = "internal"
        }
        buttonSave.setOnClickListener {
            setDevice(editTextDevice.text.toString())
            setIp(editTextIp.text.toString())
            setCheckedBox(checkedBox)
            Toast.makeText(this,"Setting Saved",Toast.LENGTH_SHORT).show()
        }

    }

    private fun setDevice(v: String) {
        val editor = getSharedPreferences("device", MODE_PRIVATE).edit()
        editor.putString("valdev", v)
        editor.apply()
    }
    private fun setIp(v: String) {
        val editor = getSharedPreferences("ip", MODE_PRIVATE).edit()
        editor.putString("valip", v)
        editor.apply()
    }
    private fun setCheckedBox(v: String) {
        val editor = getSharedPreferences("box", MODE_PRIVATE).edit()
        editor.putString("valbox", v)
        editor.apply()
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

    override fun onBackPressed() {
        val prefs1 = getSharedPreferences("device", Activity.MODE_PRIVATE)
        Export.device_name = prefs1.getString("valdev", "").toString()

        val prefs2 = getSharedPreferences("ip", Activity.MODE_PRIVATE)
        Export.ip =  prefs2.getString("valip", "").toString()

        val prefs3 = getSharedPreferences("box", Activity.MODE_PRIVATE)
        Export.checkedBox = prefs3.getString("valbox", "internal").toString()

        super.onBackPressed()
    }
}