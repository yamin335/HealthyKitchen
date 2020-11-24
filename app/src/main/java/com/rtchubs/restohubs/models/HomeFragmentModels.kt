package com.rtchubs.restohubs.models

import java.io.Serializable

data class RProductCategory(val id: Int?, val name: String?, val slug: String?,
                            val parent: Int?, val description: String?,
                            val display: String?, val image: RImage?,
                            val menu_order: Int?, val count: Int?) : Serializable

data class RImage(val id: Int?, val date_created: String?,
                   val date_created_gmt: String?, val date_modified: String?,
                   val date_modified_gmt: String?, val src: String?,
                   val name: String?, val alt: String?)

data class RProduct(val id: Int?, val name: String?, val slug: String?,
                    val type: String?, val status: String?, val featured: Boolean?,
                    val catalog_visibility: String?, val description: String?,
                    val short_description: String?, val sku: String?, val price: String?,
                    val regular_price: String?, val sale_price: String?,
                    val date_on_sale_from: Any?, val date_on_sale_from_gmt: Any?,
                    val date_on_sale_to: Any?, val date_on_sale_to_gmt: Any?,
                    val on_sale: Boolean?, val purchasable: Boolean?,
                    val total_sales: Number?, val virtual: Boolean?,
                    val downloadable: Boolean?, val button_text: String?,
                    val tax_status: String?, val tax_class: String?,
                    val manage_stock: Boolean?, val stock_quantity: Any?,
                    val stock_status: String?, val backorders: String?,
                    val backorders_allowed: Boolean?, val backordered: Boolean?,
                    val sold_individually: Boolean?, val weight: String?,
                    val shipping_required: Boolean?, val shipping_taxable: Boolean?,
                    val shipping_class: String?, val shipping_class_id: Number?,
                    val reviews_allowed: Boolean?, val average_rating: String?,
                    val rating_count: Number?, val related_ids: List<Int>?,
                    val parent_id: Number?, val purchase_note: String?,
                    val images: List<RImage>?, val menu_order: Int?)