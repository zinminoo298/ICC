package com.example.icc.Model

class ViewSummeryModel {
    var date:String? = null
    var customer:String? = null
    var business_code:String? = null
    var corner:String? = null
    var qty:String? = null
    var price:String? = null

    constructor(
        date: String?,
        customer: String?,
        business_code: String?,
        corner: String?,
        qty: String?,
        price: String?
    ) {
        this.date = date
        this.customer = customer
        this.business_code = business_code
        this.corner = corner
        this.qty = qty
        this.price = price
    }
}