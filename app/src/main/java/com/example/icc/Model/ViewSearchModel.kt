package com.example.icc.Model

class ViewSearchModel {
    var barcode:String? = null
    var qty:String? = null
    var price:String? = null

    constructor(barcode: String?, qty: String?, price: String?) {
        this.barcode = barcode
        this.qty = qty
        this.price = price
    }
}