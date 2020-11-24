package com.rtchubs.restohubs.api

import com.rtchubs.restohubs.api.Api.API_VERSION
import com.rtchubs.restohubs.api.Api.DIRECTORY_ACCOUNT
import com.rtchubs.restohubs.api.Api.DIRECTORY_BANK
import com.rtchubs.restohubs.api.Api.DIRECTORY_BANK_INFO
import com.rtchubs.restohubs.api.Api.DIRECTORY_CARD
import com.rtchubs.restohubs.api.Api.DIRECTORY_CONNECT
import com.rtchubs.restohubs.api.Api.DIRECTORY_PROFILE
import com.rtchubs.restohubs.api.Api.REPO

object Api {
    const val PROTOCOL = "https"
    const val API_ROOT = "backend.handyslash.com"
    const val API_ROOT_URL = "$PROTOCOL://$API_ROOT"
    const val REPO = "api"
    const val API_VERSION = "v1"
    const val DIRECTORY_ACCOUNT = "account"
    const val DIRECTORY_CONNECT = "connect"
    const val DIRECTORY_BANK_INFO = "bankinformation"
    const val DIRECTORY_CARD = "banklink"
    const val DIRECTORY_BANK = "cardlink"
    const val DIRECTORY_PROFILE = "profile"
    const val ContentType = "Content-Type: application/json"

    const val baseUrl = "https://healthykitchen33.com/wp-json/wc/v3/"
    const val consumerKey = "ck_0d8767f5c5aedd8d8d9716f1bcb4622411334a6b"
    const val consumerSecret = "cs_d3ac30a0271c1b2cbb144cb6c88ce0578fda6db4"
}

object ApiEndPoint {
    /* Registration */
    const val INQUIRE = "/$REPO/$API_VERSION/${DIRECTORY_ACCOUNT}/inquire"
    const val REQUESTOTP = "/$REPO/$API_VERSION/${DIRECTORY_ACCOUNT}/request-otp"
    const val REGISTRATION = "/$REPO/$API_VERSION/${DIRECTORY_ACCOUNT}"
    const val CONNECT_TOKEN = "/$REPO/$API_VERSION/${DIRECTORY_CONNECT}/token"
    const val GET_BANK_LIST = "/$REPO/$API_VERSION/${DIRECTORY_BANK_INFO}/bank-list"
    const val ADD_BANK = "/$REPO/$API_VERSION/${DIRECTORY_BANK}"
    const val ADD_CARD = "/$REPO/$API_VERSION/${DIRECTORY_CARD}"
    const val MY_ACCOUNT_LIST = "/$REPO/$API_VERSION/${DIRECTORY_PROFILE}/accounts"

    // eDokanPat
    const val ALL_MALL = "/$REPO/shopping-malls"
    const val ALL_MERCHANTS = "/$REPO/all-merchants"
    const val MERCHANT_PRODUCTS = "/$REPO/products-by-merchant/{id}"

    // RestoHubs
    const val filteredProductCategory = "products/categories"
    const val filteredProduct = "products"
}

object ResponseCodes {
    const val CODE_SUCCESS = 200
    const val CODE_TOKEN_EXPIRE = 401
    const val CODE_UNAUTHORIZED = 403
    const val CODE_VALIDATION_ERROR = 400
    const val CODE_DEVICE_CHANGE = 451
    const val CODE_FIRST_LOGIN = 426
}

object ApiCallStatus {
    const val LOADING = 0
    const val SUCCESS = 1
    const val ERROR = 2
    const val EMPTY = 3
}
