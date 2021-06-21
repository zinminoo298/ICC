package com.example.icc.Model

class ViewEditKeyModel {
    var cust:String? = null
    var prod:String? = null
    var corner:String? = null

    constructor(cust: String?, prod: String?, corner: String?) {
        this.cust = cust
        this.prod = prod
        this.corner = corner
    }
}