package com.example.icc.Model

class ViewExportModel {
    var date:String? = null
    var customer:String? = null
    var business_code:String? = null
    var corner:String? = null
    var item:String? = null
    var qty:String? = null

    constructor(
        date: String?,
        customer: String?,
        business_code: String?,
        corner: String?,
        item: String?,
        qty: String?
    ) {
        this.date = date
        this.customer = customer
        this.business_code = business_code
        this.corner = corner
        this.item = item
        this.qty = qty
    }
}