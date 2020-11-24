package com.rtchubs.restohubs.util

import android.util.Patterns
import androidx.core.text.isDigitsOnly
import com.rtchubs.restohubs.R
import java.util.regex.Pattern

/**
 * Created by Priyanka on 11/3/19.
 */

interface Validator {
    val errorMsg: Int
    fun validate(any: CharSequence?): Boolean
}

val UserNameValidator = object : Validator {
    var any: CharSequence? = null
    override val errorMsg: Int
        get() = any?.toString().userNameError() ?: R.string.error_invalid_member

    override fun validate(any: CharSequence?): Boolean {
        this.any = any
        return !any.isNullOrBlank() && any.length > 10
    }
}

val PinValidator = object : Validator {
    var any: CharSequence? = null
    override val errorMsg: Int
        get() = R.string.error_invalid_password

    override fun validate(any: CharSequence?): Boolean {
        this.any = any
        return any?.isDigitsOnly() == true && any.length in 4..6
    }
}


val PhoneValidator = object : Validator {
    override val errorMsg: Int
        get() = R.string.error_invalid_phone

    override fun validate(any: CharSequence?): Boolean =
        !any.isNullOrBlank() && isValidMobile(any)
}
val AmountValidator = object : Validator {
    var msg: Int? = null
    override val errorMsg: Int
        get() = R.string.invalid_amount

    override fun validate(any: CharSequence?): Boolean = try {
        !any.isNullOrBlank() && any.toString().toDouble() > 0.0 && DecimalTwoPlacesValidator.validate(
            any
        ) /*&& any.toString().toInt() in 10..10000000*/
    } catch (e: Exception) {
        false
    }
}
val DecimalAmountValidator = object : Validator {
    override val errorMsg: Int
        get() = R.string.invalid_amount

    override fun validate(any: CharSequence?): Boolean = try {
        !any.isNullOrBlank() && any.toString().toDouble() > 0.0 && DecimalTwoPlacesValidator.validate(
            any
        ) /*&& any.toString().toDouble() in 10f..10000000f*/
    } catch (e: Exception) {
        false
    }
}
val DecimalTwoPlacesValidator = object : Validator {
    val DECIMAL_TWO_PLACES_PATTERN = "[0-9]+(\\.[0-9][0-9]?)?"
    override val errorMsg: Int
        get() = R.string.decimal_two_places_amount_check_error

    override fun validate(any: CharSequence?): Boolean = try {
        !any.isNullOrBlank() && any.toString().toDouble() > 0.0 && Pattern.compile(
            DECIMAL_TWO_PLACES_PATTERN
        ).matcher(any).matches()
    } catch (e: Exception) {
        false
    }
}
val AccountNoValidator = object : Validator {
    override val errorMsg: Int
        get() = R.string.error_invalid_account_No

    override fun validate(any: CharSequence?): Boolean =
        any?.isDigitsOnly() == true && any.length in 10..20
}

val EmailValidator = object : Validator {
    override val errorMsg: Int
        get() = R.string.error_invalid_email

    override fun validate(any: CharSequence?): Boolean =
        !any.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(any.trim()).matches()
}

val MobileValidator = object : Validator {
    override val errorMsg: Int
        get() = R.string.error_invalid_mobile

    override fun validate(any: CharSequence?): Boolean =
        !any.isNullOrBlank() && isValidMobile(any)
}

val NidValidator = object : Validator {
    override val errorMsg: Int
        get() = R.string.error_invalid_nid

    override fun validate(any: CharSequence?): Boolean =
        any?.isDigitsOnly() == true && any.length in 10..17
}

fun isValidMobile(any: CharSequence): Boolean {
    var phoneNumber = any
    if (Patterns.PHONE.matcher(phoneNumber).matches()) {
        if (phoneNumber.length in 11..14) {
            if (phoneNumber.length == 13) {
                if (phoneNumber.startsWith("88"))
                    phoneNumber = phoneNumber.substring(2)
                else
                    return false
            } else if (phoneNumber.length == 14) {
                if (phoneNumber.startsWith("+88"))
                    phoneNumber = phoneNumber.substring(3)
                else
                    return false
            }
            if (phoneNumber.length == 11) {
                when (phoneNumber.substring(0, 3)) {
                    "013", "014", "015", "016", "017", "018", "019" -> return true
                }
            }
        }
        return false
    }
    return false
}


