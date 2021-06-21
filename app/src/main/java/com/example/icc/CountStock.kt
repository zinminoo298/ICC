package com.example.icc

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.icc.DataBase.DatabaseHandler

class CountStock : AppCompatActivity() {

    companion object {
        lateinit var textViewCust: TextView
        lateinit var textViewProd: TextView
        lateinit var textViewH: TextView
        lateinit var textViewC: TextView
        lateinit var textViewS: TextView
        lateinit var textViewDate: TextView
        lateinit var editTextQty: EditText
        lateinit var editTextBarcode: EditText
        lateinit var checkBox: CheckBox
        lateinit var buttonOk: Button
        lateinit var buttonBack: Button
        lateinit var buttonView: Button
        lateinit var buttonEdit: Button
        lateinit var textViewDesc: TextView
        lateinit var textViewItem: TextView
        lateinit var textViewRef: TextView
        lateinit var textViewQty: TextView
        lateinit var textViewPrice: TextView
        lateinit var textViewAllRec: TextView
        lateinit var textViewAllQty: TextView
        lateinit var textViewAllAmount: TextView
        lateinit var textViewBpRec: TextView
        lateinit var textViewBpQty: TextView
        lateinit var textViewBpAmount: TextView
        lateinit var textViewShRec: TextView
        lateinit var textViewShQty: TextView
        lateinit var textViewShAmount: TextView
        lateinit var db: DatabaseHandler
        internal lateinit var buttonSave : Button
        internal lateinit var buttonCancel: Button
        internal lateinit var dialog: AlertDialog
        lateinit var editTextBui: EditText
        lateinit var editTextItem: EditText
        lateinit var editTextBc: EditText
        lateinit var editTextDesc: EditText
        lateinit var editTextQt: EditText
        lateinit var editTextPrc: EditText
        var scannedBarcode = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_stock)

        textViewCust = findViewById(R.id.txt_cust)
        textViewProd = findViewById(R.id.txt_prod)
        textViewH = findViewById(R.id.txt_h)
        textViewC = findViewById(R.id.txt_c)
        textViewS = findViewById(R.id.txt_s)
        textViewDate = findViewById(R.id.txt_date)
        editTextQty = findViewById(R.id.edt_qty)
        editTextBarcode = findViewById(R.id.edt_barcode)
        checkBox = findViewById(R.id.checkbox1)
        buttonOk = findViewById(R.id.btn_ok)
        buttonBack = findViewById(R.id.btn_back)
        buttonEdit = findViewById(R.id.btn_edit)
        buttonView = findViewById(R.id.btn_view)
        textViewDesc = findViewById(R.id.txt_desc)
        textViewItem = findViewById(R.id.txt_item)
        textViewRef = findViewById(R.id.txt_ref)
        textViewQty = findViewById(R.id.txt_qty)
        textViewPrice = findViewById(R.id.txt_price)
        textViewAllRec = findViewById(R.id.all_rec)
        textViewAllQty = findViewById(R.id.all_qty)
        textViewAllAmount = findViewById(R.id.all_amount)
        textViewBpRec = findViewById(R.id.bp_rec)
        textViewBpQty = findViewById(R.id.bp_qty)
        textViewBpAmount = findViewById(R.id.bp_amount)
        textViewShRec = findViewById(R.id.sh_rec)
        textViewShQty = findViewById(R.id.sh_qty)
        textViewShAmount = findViewById(R.id.sh_amount)
        db = DatabaseHandler(this)
        db.getAll()
        db.getBP()
        db.getSH()

        textViewAllRec.text = "${DatabaseHandler.allRec}"
        textViewAllQty.text = "${DatabaseHandler.allQty}"
        textViewAllAmount.text = "${DatabaseHandler.allAmount}"
        textViewBpRec.text = "${DatabaseHandler.bpRec}"
        textViewBpQty.text = "${DatabaseHandler.bpQty}"
        textViewBpAmount.text = "${DatabaseHandler.bpAmount}"
        textViewShRec.text = "${DatabaseHandler.shRec}"
        textViewShQty.text = "${DatabaseHandler.shQty}"
        textViewShAmount.text = "${DatabaseHandler.shAmount}"

        textViewCust.text = CheckStockSetup.cust
        textViewProd.text = CheckStockSetup.prod
        textViewC.text = CheckStockSetup.c
        textViewH.text = CheckStockSetup.h
        textViewS.text = CheckStockSetup.s
        textViewDate.text = CheckStockSetup.date

        editTextQty.setText("1")
        editTextBarcode.requestFocus()
        editTextQty .isEnabled = false

        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                editTextQty .isEnabled = true
                editTextQty.requestFocus()
            } else {
                editTextQty .isEnabled = false
                editTextQty.setText("1")
                editTextBarcode.requestFocus()
            }
        }

        buttonBack.setOnClickListener {
            val myIntent = Intent(this, CheckStockSetup::class.java)
            myIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(myIntent)
        }

        buttonEdit.setOnClickListener {
            if(textViewItem.text.toString() == ""){
                Toast.makeText(this,"Please Scan Barcode",Toast.LENGTH_SHORT).show()
            }
            else{
                EditQty()
            }
        }

        buttonView.setOnClickListener {
            object : AsyncTask<Int?, Int?, Int?>() {
                internal lateinit var pgd: ProgressDialog
                override fun onPreExecute() {
                    pgd = ProgressDialog(this@CountStock)
                    pgd.setMessage("Please Wait")
                    pgd.setTitle("Loading Data")
                    pgd.show()
                    pgd.setCancelable(false)

                    super.onPreExecute()

                }

                override fun onPostExecute(result: Int?) {
                    pgd.dismiss()
                    if(DatabaseHandler.shelfCheck == 0){
                        toast()
                    }
                    else{
                        val intent = Intent(this@CountStock,ViewByShelf::class.java)
                        startActivity(intent)
                    }

                    super.onPostExecute(result)
                }

                override fun doInBackground(vararg params: Int?): Int? {
                    db.getShelfData(CheckStockSetup.cust,CheckStockSetup.prod,CheckStockSetup.c,CheckStockSetup.date)
                    db.getTotalShelfData(CheckStockSetup.cust,CheckStockSetup.prod,CheckStockSetup.c,CheckStockSetup.date)
                    return null
                }


            }.execute()
        }

        editTextQty.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (editTextQty.text.toString() == "") {
                    Toast.makeText(this, "Please Enter Quantity", Toast.LENGTH_SHORT).show()
                } else {
                    editTextBarcode.requestFocus()
                }
            }
            false
        })

        editTextBarcode.setOnKeyListener(View.OnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
//                buttonView.isEnabled = false
//                buttonEdit.isEnabled = false
//                buttonBack.isEnabled = false
                 if (editTextBarcode.text.toString() == "") {
                    Toast.makeText(this, "Please Enter Barcode", Toast.LENGTH_SHORT).show()
                } else {
                    scannedBarcode = editTextBarcode.text.toString()
                    db.loadBarcode(
                        CheckStockSetup.h,
                        editTextBarcode.text.toString(),
                        editTextQty.text.toString(),
                        "${CheckStockSetup.date}",
                        "H",
                        "${CheckStockSetup.cust}",
                        "${CheckStockSetup.c}",
                        "${CheckStockSetup.s}"
                    )
                    println(DatabaseHandler.currentQty)
                    textViewDesc.text = "${DatabaseHandler.description}"
                    textViewItem.text = "${DatabaseHandler.prod}${DatabaseHandler.item}"
                    textViewRef.text = "${DatabaseHandler.ref}"
                    textViewPrice.text = "${DatabaseHandler.price}"
                    textViewQty.text = "${DatabaseHandler.currentQty}"

                    if (DatabaseHandler.prod == "Master Not Found") {
                        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                        NewItemDialog()
                    } else {
                        db.getAll()
                        db.getBP()
                        db.getSH()

                        textViewAllRec.text = "${DatabaseHandler.allRec}"
                        textViewAllQty.text = "${DatabaseHandler.allQty}"
                        textViewAllAmount.text = "${DatabaseHandler.allAmount}"
                        textViewBpRec.text = "${DatabaseHandler.bpRec}"
                        textViewBpQty.text = "${DatabaseHandler.bpQty}"
                        textViewBpAmount.text = "${DatabaseHandler.bpAmount}"
                        textViewShRec.text = "${DatabaseHandler.shRec}"
                        textViewShQty.text = "${DatabaseHandler.shQty}"
                        textViewShAmount.text = "${DatabaseHandler.shAmount}"

                    }
//                    editTextBarcode.text.clear()

                    if (checkBox.isChecked) {
                        editTextBarcode.nextFocusDownId= editTextQty.id
                        editTextQty.requestFocus()
                    } else {
                        editTextBarcode.requestFocus()
                        editTextBarcode.selectAll()
                        editTextBarcode.nextFocusDownId= editTextBarcode.id
                    }
                }
//                val handler = Handler()
//                handler.postDelayed(Runnable {
//                    buttonView.isEnabled = true
//                    buttonEdit.isEnabled = true
//                    buttonBack.isEnabled = true
//                }, 1) // 5000ms
            }
            false
        })

        editTextBarcode.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editTextBarcode.text.toString() == "") {
                    Toast.makeText(this, "Please Enter Barcode", Toast.LENGTH_SHORT).show()
                } else {
                    scannedBarcode = editTextBarcode.text.toString()
                    db.loadBarcode(
                        CheckStockSetup.h,
                        editTextBarcode.text.toString(),
                        editTextQty.text.toString(),
                        "${CheckStockSetup.date}",
                        "H",
                        "${CheckStockSetup.cust}",
                        "${CheckStockSetup.c}",
                        "${CheckStockSetup.s}"
                    )
                    println(DatabaseHandler.currentQty)
                    textViewDesc.text = "${DatabaseHandler.description}"
                    textViewItem.text = "${DatabaseHandler.prod}${DatabaseHandler.item}"
                    textViewRef.text = "${DatabaseHandler.ref}"
                    textViewPrice.text = "${DatabaseHandler.price}"
                    textViewQty.text = "${DatabaseHandler.currentQty}"

                    if (DatabaseHandler.prod == "Master Not Found") {
                        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                        NewItemDialog()
                    } else {
                        db.getAll()
                        db.getBP()
                        db.getSH()

                        textViewAllRec.text = "${DatabaseHandler.allRec}"
                        textViewAllQty.text = "${DatabaseHandler.allQty}"
                        textViewAllAmount.text = "${DatabaseHandler.allAmount}"
                        textViewBpRec.text = "${DatabaseHandler.bpRec}"
                        textViewBpQty.text = "${DatabaseHandler.bpQty}"
                        textViewBpAmount.text = "${DatabaseHandler.bpAmount}"
                        textViewShRec.text = "${DatabaseHandler.shRec}"
                        textViewShQty.text = "${DatabaseHandler.shQty}"
                        textViewShAmount.text = "${DatabaseHandler.shAmount}"
                        if (checkBox.isChecked) {
                            editTextQty.requestFocus()
                        } else {
                            editTextBarcode.requestFocus()
                        }
//                        editTextBarcode.text.clear()
                    }
                }
            }
            false
        })

        buttonOk.setOnClickListener {
            if (editTextBarcode.text.toString() == "") {
                Toast.makeText(this, "Please Enter Barcode", Toast.LENGTH_SHORT).show()
            } else {
                scannedBarcode = editTextBarcode.text.toString()
                db.loadBarcode(
                    CheckStockSetup.h,
                    editTextBarcode.text.toString(),
                    editTextQty.text.toString(),
                    "${CheckStockSetup.date}",
                    "H",
                    "${CheckStockSetup.cust}",
                    "${CheckStockSetup.c}",
                    "${CheckStockSetup.s}"
                )
                textViewDesc.text = "${DatabaseHandler.description}"
                textViewItem.text = "${DatabaseHandler.prod}${DatabaseHandler.item}"
                textViewRef.text = "${DatabaseHandler.ref}"
                textViewPrice.text = "${DatabaseHandler.price}"
                textViewQty.text = "${DatabaseHandler.currentQty}"

                if(DatabaseHandler.prod == "Master Not Found"){
                    val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 2000)
                    NewItemDialog()
                }
                else{
                    db.getAll()
                    db.getBP()
                    db.getSH()

                    textViewAllRec.text = "${DatabaseHandler.allRec}"
                    textViewAllQty.text = "${DatabaseHandler.allQty}"
                    textViewAllAmount.text = "${DatabaseHandler.allAmount}"
                    textViewBpRec.text = "${DatabaseHandler.bpRec}"
                    textViewBpQty.text = "${DatabaseHandler.bpQty}"
                    textViewBpAmount.text = "${DatabaseHandler.bpAmount}"
                    textViewShRec.text = "${DatabaseHandler.shRec}"
                    textViewShQty.text = "${DatabaseHandler.shQty}"
                    textViewShAmount.text = "${DatabaseHandler.shAmount}"
                    if (checkBox.isChecked) {
                        editTextQty.requestFocus()
                    } else {
                        editTextBarcode.requestFocus()
                    }
                    editTextBarcode.text.clear()
                }

            }
        }
    }

    fun toast(){
        Toast.makeText(this,"No Data",Toast.LENGTH_SHORT).show()
    }

    fun NewItemDialog(){
        val builder = AlertDialog.Builder(this)

        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.new_item, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.setMessage("New Item")
        dialog.show()
        dialog.setCancelable(false)

        buttonSave = view.findViewById(R.id.btn_save)
        buttonCancel = view.findViewById(R.id.btn_canel)
        editTextBui = view.findViewById(R.id.edt_bui)
        editTextItem = view.findViewById(R.id.edt_item)
        editTextBc = view.findViewById(R.id.edt_bc)
        editTextDesc = view.findViewById(R.id.edt_desc)
        editTextQt = view.findViewById(R.id.edt_qty)
        editTextPrc = view.findViewById(R.id.edt_price)

        editTextBui.setText(CheckStockSetup.prod)
        editTextItem.setText(editTextBarcode.text.toString())
        editTextBc.setText(editTextBarcode.text.toString())

        buttonSave.setOnClickListener {
            if(editTextBui.text.toString()=="" || editTextItem.text.toString()==""|| editTextBc.text.toString()=="" || editTextDesc.text.toString()=="" || editTextQt.text.toString()=="" || editTextPrc.text.toString()=="" ){
                Toast.makeText(this, "Please Enter Required Data", Toast.LENGTH_SHORT).show()
            }
            else{
                db.addNew(
                    CheckStockSetup.h,
                    editTextBc.text.toString(),
                    editTextQt.text.toString(),
                    "${CheckStockSetup.date}",
                    "H",
                    "${CheckStockSetup.cust}",
                    "${CheckStockSetup.c}",
                    "${CheckStockSetup.s}",
                    editTextItem.text.toString(),
                    editTextPrc.text.toString(),
                    editTextDesc.text.toString()
                )

                textViewDesc.text = editTextDesc.text.toString()
                textViewItem.text = editTextBui.text.toString()+editTextItem.text.toString()
                textViewRef.text = "${DatabaseHandler.reference}"
                textViewPrice.text = editTextPrc.text.toString()
                textViewQty.text = editTextQt.text.toString()
                DatabaseHandler.item = editTextItem.text.toString()

                db.getAll()
                db.getBP()
                db.getSH()

                textViewAllRec.text = "${DatabaseHandler.allRec}"
                textViewAllQty.text = "${DatabaseHandler.allQty}"
                textViewAllAmount.text = "${DatabaseHandler.allAmount}"
                textViewBpRec.text = "${DatabaseHandler.bpRec}"
                textViewBpQty.text = "${DatabaseHandler.bpQty}"
                textViewBpAmount.text = "${DatabaseHandler.bpAmount}"
                textViewShRec.text = "${DatabaseHandler.shRec}"
                textViewShQty.text = "${DatabaseHandler.shQty}"
                textViewShAmount.text = "${DatabaseHandler.shAmount}"

                dialog.dismiss()

                if (checkBox.isChecked) {
                    editTextQty.requestFocus()
                } else {
                    editTextBarcode.requestFocus()
                }

            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun EditQty(){
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val view = inflater.inflate(R.layout.edit_qty, null)
        builder.setView(view)
        dialog = builder.create()
        dialog.setMessage("Edit Quantity")
        dialog.show()
        dialog.setCancelable(false)

        buttonSave = view.findViewById(R.id.btn_save)
        buttonCancel = view.findViewById(R.id.btn_canel)
        editTextBui = view.findViewById(R.id.edt_bui)
        editTextItem = view.findViewById(R.id.edt_item)
        editTextBc = view.findViewById(R.id.edt_bc)
        editTextDesc = view.findViewById(R.id.edt_desc)
        editTextQt = view.findViewById(R.id.edt_qty)
        editTextPrc = view.findViewById(R.id.edt_price)

        editTextBui.setText(CheckStockSetup.prod)
        editTextItem.setText(DatabaseHandler.item)
        editTextBc.setText(scannedBarcode)
        editTextDesc.setText(textViewDesc.text.toString())
        editTextQt.setText(textViewQty.text.toString())
        editTextPrc.setText(textViewPrice.text.toString())

        buttonSave.setOnClickListener {
            if(editTextBui.text.toString()=="" || editTextItem.text.toString()==""|| editTextBc.text.toString()=="" || editTextDesc.text.toString()=="" || editTextQt.text.toString()=="" || editTextPrc.text.toString()=="" ){
                Toast.makeText(this, "Please Enter Qty", Toast.LENGTH_SHORT).show()
            }
            else{
                db.updateQty(editTextQt.text.toString(), DatabaseHandler.item)
                textViewQty.text = editTextQt.text.toString()

                db.getAll()
                db.getBP()
                db.getSH()

                textViewAllRec.text = "${DatabaseHandler.allRec}"
                textViewAllQty.text = "${DatabaseHandler.allQty}"
                textViewAllAmount.text = "${DatabaseHandler.allAmount}"
                textViewBpRec.text = "${DatabaseHandler.bpRec}"
                textViewBpQty.text = "${DatabaseHandler.bpQty}"
                textViewBpAmount.text = "${DatabaseHandler.bpAmount}"
                textViewShRec.text = "${DatabaseHandler.shRec}"
                textViewShQty.text = "${DatabaseHandler.shQty}"
                textViewShAmount.text = "${DatabaseHandler.shAmount}"

                dialog.dismiss()

                if (checkBox.isChecked) {
                    editTextQty.requestFocus()
                } else {
                    editTextBarcode.requestFocus()
                }

            }
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
}
