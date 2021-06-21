package com.example.icc.Model

class ViewShelfModel {
    var location:String? = null
    var qty:String? = null
    var price:String? = null

    constructor(location: String?, qty: String?, price: String?) {
        this.location = location
        this.qty = qty
        this.price = price
    }
}