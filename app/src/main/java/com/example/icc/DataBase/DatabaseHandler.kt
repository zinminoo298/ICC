package com.example.icc.DataBase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.icc.Adapters.ViewShelfAdapter
import com.example.icc.CheckStockSetup
import com.example.icc.Model.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHandler(val context: Context) {

    companion object{
        val REAL_DATABASE = "database.db"
        var ViewMaster = ArrayList<ViewModel>()
        var ViewSummery = ArrayList<ViewSummeryModel>()
        var ViewExport = ArrayList<ViewExportModel>()
        var ViewEdit = ArrayList<ViewEditKeyModel>()
        var ViewShelf = ArrayList<ViewShelfModel>()
        var ViewSearch = ArrayList<ViewSearchModel>()
        var ViewFilter = ArrayList<ViewFilterSearchModel>()
        var description = ""
        var prod = ""
        var cat = ""
        var s_price=""
        var item = ""
        var price = ""
        var ref = ""
        var currentQty = ""
        var allRec = 0
        var allQty = 0
        var allAmount = "0.00"
        var bpRec = 0
        var bpQty = 0
        var bpAmount = "0.00"
        var shRec= 0
        var shQty = 0
        var shAmount = ""
        var dateList = ArrayList<String>()
        var custList = ArrayList<String>()
        var buiList = ArrayList<String>()
        var cornerList = ArrayList<String>()
        var shelfList = ArrayList<String>()
        var totalRec = ""
        var totalQty = ""
        var totalPrice = ""
        var masterRec = 0
        var category = ""
        var reference = ""
        var mainQty = 0
        var mainItem = 0
        var mainAmount = ""
        var editKeyCheck = 0
        var viewDataCheck = 0
        var editKeyCount=0
        var shelfCheck = 0
        var searchCheck = 0
        var shelfTotal = ""
        var shelfQty = ""
        var shelfPrice = ""
        var searchTotal = ""
        var searchQty = ""
        var searchPrice=""
    }

    /*Open database and start copying database from assets folder*/
    fun openDatabase(): SQLiteDatabase {
        val dbFile=context.getDatabasePath(REAL_DATABASE)
        if (!dbFile.exists()) {
            try {
                val checkDB=context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)

                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }
        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    /*Copy database from assets folder to package*/
    @SuppressLint("WrongConstant")
    private fun copyDatabase(dbFile: File) {
        val `is`=context.assets.open(REAL_DATABASE)
        val os= FileOutputStream(dbFile)

        val buffer=ByteArray(1024)
        while(`is`.read(buffer)>0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        `is`.close()
        Log.d("#DB", "completed..")
    }

    fun getMainSummery(){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT sum(Qty),count(*),sum(Qty*Price) from transaction_table "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            mainQty = cursor.getInt(0)
            mainItem = cursor.getInt(1)
            mainAmount = String.format("%.2f",cursor.getDouble(2))
        }
    }

    fun countMasterRec(){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*) from master "
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            masterRec = cursor.getInt(0)
        }
        else{
            masterRec = 0
        }
    }

    fun loadEditKey(){
        ViewEdit.clear()
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT Customer,Business_Product,Corner From transaction_table GROUP By Customer,Business_Product,Corner"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            editKeyCheck  = 1
            do{
                val data = ViewEditKeyModel(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2))
                ViewEdit.add(data)
            }while (cursor.moveToNext())
            editKeyCount = cursor.count
        }
        else{
            editKeyCount = 0
            editKeyCheck  = 0
        }
        db.close()
    }

    fun loadSummery(){
        ViewSummery.clear()
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT Date,Customer,Business_Product,Corner,SUM(Qty),SUM(Price*Qty) FROM transaction_table GROUP BY  Date , Customer,Business_Product,Corner"
        val cursor = db.rawQuery(query,null)
        var count = 0
        if(cursor.moveToFirst()){
            do{
                count++
                println(count)
                val data = ViewSummeryModel(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5))
                ViewSummery.add(data)
            }while (cursor.moveToNext())
        }

        val query1 = "SELECT count(DISTINCT Date),count(DISTINCT Customer),count(DISTINCT Business_Product),count(DISTINCT Corner),sum(Qty),sum(Price*Qty) FROM transaction_table"
        val cursor1 = db.rawQuery(query1,null)
        if (cursor1.moveToFirst()){
            val totaldate = cursor1.getInt(0)
            val totalcust = cursor1.getInt(1)
            val totalbui = cursor1.getInt(2)
            val totalcorner = cursor1.getInt(3)
            totalQty = cursor1.getString(4)
            totalPrice = cursor1.getString(5)
            println("date"+totaldate)
            println("cust"+totalcust)
            println("bui"+totalbui)
            println("corner"+totalcorner)
            val amplitudes = listOf(totaldate,totalcust,totalbui,totalcorner)
            val max = amplitudes.maxOrNull() ?: 0
            println("max"+max)
            totalRec = max.toString()
        }


    }

    fun loadExport(){
        ViewExport.clear()
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT Date,Customer,Business_Product,Corner,Count(Barcode),SUM(Qty) FROM transaction_table GROUP BY  Date , Customer,Business_Product,Corner"
        val cursor = db.rawQuery(query,null)
        var count = 0
        if(cursor.moveToFirst()){
            do{
                count++
                println(count)
                val data = ViewExportModel(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5))
                ViewExport.add(data)
            }while (cursor.moveToNext())
        }
    }

    fun loadMaster(){
        ViewMaster.clear()
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM master"
        val cursor = db.rawQuery(query,null)
        var count = 0
        if(cursor.moveToFirst()){
            do{
                count++
                println(count)
                val data = ViewModel(cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7))
                ViewMaster.add(data)
            }while (cursor.moveToNext())
        }
    }

    fun addNew(handcode:String,barcode:String,qty:String,date:String,check:String,cust:String,corner:String,shelf:String,itemCode:String,prc:String,desc:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT Category, Reference From master"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            category = cursor.getString(0)
            reference = cursor.getString(1)
        }

        val values = ContentValues()
        values.put("Hand_Code",handcode)
        values.put("Business_Product", CheckStockSetup.prod)
        values.put("Product_Code",itemCode)
        values.put("Barcode",barcode)
        values.put("Qty",qty)
        values.put("Category",category)
        values.put("Price", prc)
        values.put("S_Price", "")
        values.put("Description", desc)
        values.put("Reference",reference)
        values.put("Date",date)
        values.put("Check_type",check)
        values.put("Customer",cust)
        values.put("Corner",corner)
        values.put("Shelf",shelf)
        db.insertWithOnConflict("transaction_table", null, values, SQLiteDatabase.CONFLICT_IGNORE)

        val values1 = ContentValues()
        values1.put("Business_Product", CheckStockSetup.prod)
        values1.put("Product_Code",itemCode)
        values1.put("Barcode",barcode)
        values1.put("Category",category)
        values1.put("Price", prc)
        values1.put("S_Price", "")
        values1.put("Description", desc)
        values1.put("Reference",reference)
        db.insertWithOnConflict("master", null, values1, SQLiteDatabase.CONFLICT_IGNORE)

        db.close()
    }

    fun updateQty(qty:String,barcode:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val values=ContentValues()
        values.put("Qty", qty)
        db.update("transaction_table", values, "Hand_Code=? AND Business_Product=? AND Product_Code=? AND Customer=? AND Corner=? AND Shelf=? AND Date=?", arrayOf(CheckStockSetup.h,CheckStockSetup.prod
            ,barcode,CheckStockSetup.cust,CheckStockSetup.c,CheckStockSetup.s,CheckStockSetup.date))
        db.close()
    }

    fun updateKey(cust:String,prod:String,corner:String,oldcust:String,oldprod:String,oldcorner:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val values=ContentValues()
        values.put("Customer", cust)
        values.put("Business_Product", prod)
        values.put("Corner", corner)
        db.update("transaction_table", values, "Customer=? AND Business_Product=? AND Corner=?", arrayOf(oldcust,oldprod,oldcorner))
        db.close()
    }

    fun loadBarcode(handcode:String,barcode:String,qty:String,date:String,check:String,cust:String,corner:String,shelf:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT * FROM master WHERE Barcode='$barcode' AND Business_Product = '${CheckStockSetup.prod}'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            prod = cursor.getString(0)
            item = cursor.getString(1)
            cat = cursor.getString(3)
            price = cursor.getString(4)
            s_price = cursor.getString(5)
            description = cursor.getString(6)
            ref = cursor.getString(7)

            addBarcode(handcode,barcode,qty,date,check,cust,corner,shelf)
        }
        else{
            prod = "Master Not Found"
            item = ""
            cat = ""
            price = ""
            s_price = ""
            description = ""
            ref = ""
            currentQty = ""
            println("NotFound")
        }
    }

    fun addBarcode(handcode:String,barcode:String,qty:String,date:String,check:String,cust:String,corner:String,shelf:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val values = ContentValues()
        values.put("Hand_Code",handcode)
        values.put("Business_Product", CheckStockSetup.prod)
        values.put("Product_Code",item)
        values.put("Barcode",barcode)
        values.put("Qty",qty)
        values.put("Category",cat)
        values.put("Price", price)
        values.put("S_Price", s_price)
        values.put("Description", description)
        values.put("Reference",ref)
        values.put("Date",date)
        values.put("Check_type",check)
        values.put("Customer",cust)
        values.put("Corner",corner)
        values.put("Shelf",shelf)
        val id=db.insertWithOnConflict("transaction_table", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        currentQty = qty
        if(id == -1L){
            val query = "SELECT Qty FROM transaction_table WHERE Product_Code='$item' AND Corner='$corner' AND SHELF = '$shelf' AND Business_Product= '${CheckStockSetup.prod}'"
            val cursor = db.rawQuery(query,null)
            if(cursor.moveToFirst()){
                val currentqty = cursor.getInt(0)
                println("current"+currentqty)
                val updateQty:Int = (currentqty + Integer.parseInt(qty))
                currentQty = updateQty.toString()
                println("current"+currentqty)
                val values = ContentValues()
                values.put("Qty",updateQty)
                db.update("transaction_table",values,"Product_Code='$item' AND Corner='$corner' AND SHELF = '$shelf'",null)
            }
            else{
                println("NONO")
            }
        }
    }

    fun getAll(){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*),sum(Qty),sum(Price*Qty) FROM transaction_table"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            allRec = cursor.getInt(0)
            allQty = cursor.getInt(1)
            allAmount =String.format("%.2f",cursor.getDouble(2))
        }
        else{
            allRec = 0
            allQty = 0
            allAmount = "0.00"
        }
    }

    fun getBP(){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*),sum(Qty),sum(Price*Qty) FROM transaction_table WHERE Business_Product='${CheckStockSetup.prod}'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            bpRec = cursor.getInt(0)
            bpQty = cursor.getInt(1)
            bpAmount = String.format("%.2f",cursor.getDouble(2))
        }
        else{
            bpRec = 0
            bpQty = 0
            bpAmount = "0.00"
        }
    }

    fun getSH(){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT count(*),sum(Qty),sum(Price*Qty) FROM transaction_table WHERE Business_Product='${CheckStockSetup.prod}' AND Shelf='${CheckStockSetup.s}'"
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            shRec = cursor.getInt(0)
            shQty = cursor.getInt(1)
            shAmount = String.format("%.2f",cursor.getDouble(2))
        }
        else{
            shRec = 0
            shQty = 0
            shAmount ="0.00"
        }
    }

    fun getDate(){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT Date FROM transaction_table GROUP BY Date"
        val cursor = db.rawQuery(query,null)
        dateList.clear()
        dateList.add("Select Date")
        if(cursor.moveToFirst()){
            viewDataCheck = 1
            do{
                dateList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
            viewDataCheck = 0
        }
        db.close()
    }

    fun getOthers(date:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT DISTINCT Customer FROM transaction_table WHERE date = '$date'"
        val query1 = "SELECT DISTINCT Business_Product FROM transaction_table WHERE date = '$date'"
        val query2 = "SELECT DISTINCT Corner FROM transaction_table WHERE date = '$date'"
        val query3 = "SELECT DISTINCT Shelf FROM transaction_table WHERE date = '$date'"
        val cursor = db.rawQuery(query,null)
        shelfList.clear()
        custList.clear()
        buiList.clear()
        cornerList.clear()

        shelfList.add("Select Shelf")
        custList.add("Select Cust")
        buiList.add("Select Prd")
        cornerList.add("Select Corner")
        if(cursor.moveToFirst()){
            do{
                custList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
        }

        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            do{
                buiList.add(cursor1.getString(0))
            }while (cursor1.moveToNext())
        }
        else{
        }

        val cursor2 = db.rawQuery(query2,null)
        if(cursor2.moveToFirst()){
            do{
                cornerList.add(cursor2.getString(0))
            }while (cursor2.moveToNext())
        }
        else{
        }

        val cursor3 = db.rawQuery(query3,null)
        if(cursor3.moveToFirst()){
            do{
                shelfList.add(cursor3.getString(0))
            }while (cursor3.moveToNext())
        }
        else{
        }
        db.close()
    }

    fun getCorner(cust:String,date:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT DISTINCT Corner FROM transaction_table WHERE Customer = '$cust' AND Date='$date'"
        val query1 = "SELECT DISTINCT Shelf FROM transaction_table WHERE Customer = '$cust' AND Date='$date'"
        val cursor = db.rawQuery(query,null)
        cornerList.clear()
        cornerList.add("Select Corner")
        if(cursor.moveToFirst()){
            do{
                cornerList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
        }

        val cursor1 = db.rawQuery(query1,null)
        shelfList.clear()
        shelfList.add("Select Shelf")
        if(cursor1.moveToFirst()){
            do{
                shelfList.add(cursor1.getString(0))
            }while (cursor.moveToNext())
        }
        else{
        }
    }

    fun getShelf(corner:String,cust:String,date:String){
        val db = context.openOrCreateDatabase(REAL_DATABASE,Context.MODE_PRIVATE,null)
        val query = "SELECT DISTINCT Shelf FROM transaction_table WHERE Corner = '$corner' AND Customer = '$cust' AND Date='$date'"
        val cursor = db.rawQuery(query,null)
        shelfList.clear()
        shelfList.add("Select Shelf")
        if(cursor.moveToFirst()){
            do{
                shelfList.add(cursor.getString(0))
            }while (cursor.moveToNext())
        }
        else{
        }
    }

    fun getShelfData(cust: String, bui:String, corner: String, date: String) {
        ViewShelf.clear()
        val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query =
            "SELECT Shelf,SUM(Qty),SUM(Qty*Price) FROM transaction_table WHERE Customer = '$cust' AND Business_Product = '$bui' AND Corner='$corner' AND Date='$date' Group By Shelf"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            shelfCheck = 1
            do {
                val data = ViewShelfModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
                ViewShelf.add(data)
            } while (cursor.moveToNext())
        } else {
            shelfCheck = 0
        }
    }

    fun getTotalShelfData(cust: String, bui:String, corner: String, date: String){
        val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query =
            "SELECT count(DISTINCT Shelf),SUM(Qty),SUM(Qty*Price) FROM transaction_table WHERE Customer = '$cust' AND Business_Product = '$bui' AND Corner='$corner' AND Date='$date' Group By Corner "
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            shelfCheck = 1
            shelfTotal = cursor.getString(0)
            shelfQty = cursor.getString(1)
            shelfPrice = cursor.getString(2)

        } else {
            shelfCheck = 0
        }
    }

    fun getSearch(cust: String, bui:String, corner: String, date: String, shelf: String){
        val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)
        val query =
            "SELECT Barcode,SUM(Qty),SUM(Qty*Price) FROM transaction_table WHERE Customer = '$cust' AND Business_Product = '$bui' AND Corner='$corner' AND Shelf='$shelf' AND Date='$date' Group By Barcode "
        val query1 = "SELECT count(*),SUM(Qty),SUM(Qty*Price) FROM transaction_table WHERE Customer = '$cust' AND Business_Product = '$bui' AND Corner='$corner' AND Shelf='$shelf' AND Date='$date' Group By Shelf"
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            ViewSearch.clear()
            searchCheck = 1
            do{
                val data = ViewSearchModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
                )
                ViewSearch.add(data)
            }while (cursor.moveToNext())
        }
        else{
            searchCheck = 0
        }

        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            do{
                searchTotal = cursor1.getString(0)
                searchQty = cursor1.getString(1)
                searchPrice = cursor1.getString(2)
            }while (cursor1.moveToNext())
        }
        db.close()
    }

    fun getSearchFilter(cust: String, bui:String, corner: String, date: String, shelf: String){
        var hasCust = ""
        var hasBui = ""
        var hasCorner = ""
        var hasShelf = ""
        val db = context.openOrCreateDatabase(REAL_DATABASE, Context.MODE_PRIVATE, null)

        if(cust == "Select Cust"){
            hasCust = ""
        }
        else{
            hasCust = "Customer = '$cust' AND "
        }

        if(bui == "Select Prd"){
            hasBui = ""
        }
        else{
            hasBui = "Business_Product = '$bui' AND "
        }

        if(corner == "Select Corner"){
            hasCorner = ""
        }
        else{
            hasCorner = "Corner = '$corner' AND "
        }

        if(shelf == "Select Shelf"){
            hasShelf = ""
        }
        else{
            hasShelf = "Shelf='$shelf' AND "
        }
        val query = "SELECT Shelf,Barcode,SUM(Qty),SUM(Qty*Price) FROM transaction_table WHERE $hasCust$hasBui$hasCorner$hasShelf Date='$date' Group By Barcode,Shelf Order By Shelf,Barcode"
        val query1 = "SELECT count(*),SUM(Qty),SUM(Qty*Price) FROM transaction_table WHERE $hasCust$hasBui$hasCorner$hasShelf Date='$date' Group By Shelf"
        println(query)
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            ViewFilter.clear()
            searchCheck = 1
            do{
                val data = ViewFilterSearchModel(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3)
                )
                ViewFilter.add(data)
            }while (cursor.moveToNext())
        }
        else{
            searchCheck = 0
        }

        val cursor1 = db.rawQuery(query1,null)
        if(cursor1.moveToFirst()){
            do{
                searchTotal = cursor1.getString(0)
                searchQty = cursor1.getString(1)
                searchPrice = cursor1.getString(2)
            }while (cursor1.moveToNext())
        }
        db.close()
    }

}