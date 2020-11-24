package com.rtchubs.restohubs.models

class Bank {
    var bankInformationId: Int = 0
    var name: String = ""
    var imageUrl: String = ""
    var banglaName: Any = ""

    @field:JvmField
    var isCardRelatedBank: Boolean = false
    var bankCode: String = ""
    var routingNumber: String = ""
    var createdAt: String = ""
    var createdBy: Any = ""
    var lastUpdatedAt: String = ""
    var lastUpdatedBy: Any = ""

    @field:JvmField
    var isCardLinkAllowed: Boolean = false

    @field:JvmField
    var isBankLinkAllowed: Boolean = false
}