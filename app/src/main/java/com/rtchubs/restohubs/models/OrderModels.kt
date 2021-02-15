package com.rtchubs.restohubs.models

data class Order(val customer_id: Int?, val customer_name: String?, val customer_mobile: String?,
                 val address: String?, val city: String?, val state: String?, val zipcode: String?,
                 val status: String?, val YourReference: String?, val date: String?,
                 val paid_amount: Int?, val details: ArrayList<OrderItem>?)

data class OrderItem(val product_id: Int?, val product_name: String?, val qty: Int?,
                     val unit_price: Int?, val sub_total: Int?)

data class OrderResponse(val code: Int?, val status: String?, val message: String?, val data: OrderResponseData?)

data class OrderResponseData(val order: OrderResponseOrder?)

data class OrderResponseOrderItem(val id: Number?, val sale_id: Number?, val product_id: Number?,
                            val description: String?, val linkTo: Any?, val unitType: String?,
                            val qty: Number?, val total_received: Number?, val return_qty: Number?,
                            val unit_price: Number?, val taxType: Any?, val taxTypeValue: Any?,
                            val discountType: Any?, val discountTypeValue: Any?, val sub_total: Number?,
                            val type: Any?, val created_at: String?, val updated_at: String?, val product_name: String?)

data class OrderResponseOrder(val id: Number?, val customer_id: Number?, val customer_name: String?, val date: String?,
                 val status: String?, val YourReference: String?, val OurReference: Any?, val amount_are: String?,
                 val customer_note: Any?, val branch_id: Any?, val cart_total: Number?, val discount_amount: Any?,
                 val discount: Any?, val discount_type: String?, val tax: Any?, val tax_amount: Any?, val vat: Any?,
                 val vat_amount: Any?, val tax_type_total: Any?, val discount_total: Any?, val grand_total: Number?,
                 val buying_total: Number?, val paid_amount: Number?, val due_amount: Any?, val delivered_at: Any?,
                 val paid_at: Any?, val file_name: String?, val created_at: String?, val updated_at: String?,
                 val customer_mobile: String?, val address: String?, val city: String?, val state: String?,
                 val zipcode: String?, val details: ArrayList<OrderResponseOrderItem>?)