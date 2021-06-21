package com.example.icc.Model

class ViewFilterSearchModel {
    var shelf:String? = null
    var barcode:String? = null
    var qty:String? = null
    var price:String? = null

    constructor(shelf: String?, barcode: String?, qty: String?, price: String?) {
        this.shelf = shelf
        this.barcode = barcode
        this.qty = qty
        this.price = price
    }
}