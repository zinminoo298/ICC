package com.example.icc.Model

class ViewModel {
    var businessProduct:String? = null
    var productCode:String? = null
    var barcode:String? = null
    var category:String? = null
    var price:String? = null
    var s_Price:String? = null
    var description:String? = null
    var reference:String? = null

    constructor(
        businessProduct: String?,
        productCode: String?,
        barcode: String?,
        category: String?,
        price: String?,
        s_Price: String?,
        description: String?,
        reference: String?
    ) {
        this.businessProduct = businessProduct
        this.productCode = productCode
        this.barcode = barcode
        this.category = category
        this.price = price
        this.s_Price = s_Price
        this.description = description
        this.reference = reference
    }
}