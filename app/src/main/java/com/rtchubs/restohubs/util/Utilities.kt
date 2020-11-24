package com.rtchubs.restohubs.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.rtchubs.restohubs.R
import java.io.*
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Priyanka on 5/23/2017.
 */

object Utilities {
    val COUNTRY_CODE = "+88"
    val COUNTRY_CODE_WP = "88"
    val PUSH_NOTIFICATION = "pushNotification"
    val DATABASE_CHANGED = "securepenny.com.psb.DATABASE_CHANGED"


    // id to handle the notification in the notification tray
    internal val NOTIFICATION_ID = 10001
    internal val NOTIFICATION_ID_BIG_IMAGE = 101

    private val dateFormat: String? = null
    val deviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    fun hideKeyboard(activity: Activity) {
        val view = activity.findViewById<View>(android.R.id.content)
        view?.postDelayed({
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }, 100)
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }


    fun isValidMobile(phoneNumber: String?): Boolean {
        var phoneNumber = phoneNumber
        if (TextUtils.isEmpty(phoneNumber)) {
            return false
        } /*else if (TextUtils.isDigitsOnly(phoneNumber)) {*/
        else if (phoneNumber != null) {
            if (phoneNumber.length > 10 && phoneNumber.length < 15) {
                if (phoneNumber.length == 13) {
                    if (phoneNumber.startsWith(COUNTRY_CODE_WP))
                        phoneNumber = phoneNumber.substring(2)
                    else
                        return false
                } else if (phoneNumber.length == 14) {
                    if (phoneNumber.startsWith(COUNTRY_CODE))
                        phoneNumber = phoneNumber.substring(3)
                    else
                        return false
                }
                if (phoneNumber.length == 11) {
                    return isStartedWithMobile(phoneNumber)
                }
            }
        }
        return false
        /*}
        return false;*/
    }

    fun isStartedWithMobile(phoneNumber: String): Boolean {
        when (phoneNumber.substring(0, 3)) {
            "011", "013", "015", "016", "017", "018", "019", "014" -> return true
        }
        return false
    }

    fun isValidPhone(phoneNumber: String): Boolean {
        return !TextUtils.isEmpty(phoneNumber) && TextUtils.isDigitsOnly(phoneNumber) && phoneNumber.length > 5 && phoneNumber.length < 16
    }

    fun isValidWebAddress(webAddress: String): Boolean {
        val matcher = Patterns.WEB_URL.matcher(webAddress)
        return matcher.matches()

    }


    fun isValidPassword(password: String): Boolean {
        val PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~])[a-zA-Z0-9!\"#$%&'()*+,-.:;" + "<=>?@\\[\\]^_`{|}~]{10,15})"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(password)
        return matcher.matches()
    }


    fun logoutUser() {

        //EventBus.getDefault().post(LogoutEvent())
    }

    fun showSuccessSnack(parentView: View, message: String) {
        showSuccessSnack(parentView, message, Snackbar.LENGTH_SHORT)
    }

    @JvmOverloads
    fun showErrorSnack(parentView: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        try {
            val sb = Snackbar.make(parentView, message, duration)
            val view = sb.view
            val tv = view.findViewById<TextView>(R.id.snackbar_text)
            tv.setTextColor(Color.WHITE)
            view.setBackgroundColor(ContextCompat.getColor(parentView.context, R.color.colorRed))
            sb.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showSuccessSnack(parentView: View, message: String, duration: Int) {
        try {
            val sb = Snackbar.make(parentView, message, duration)
            val view = sb.view
            val tv = view.findViewById<View>(R.id.snackbar_text) as TextView
            tv.setTextColor(Color.WHITE)
            view.setBackgroundColor(ContextCompat.getColor(parentView.context, R.color.green))
            sb.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun getFormattedMobile(mobileNumber: String): String {
        return mobileNumber.replace("(?<=\\w{5})\\w(?=\\w{3})".toRegex(), "*")
    }

    fun getFormattedEmail(email: String): String {
        return email.replace("(?<=.{3}).(?=.*@)".toRegex(), "*")
    }

    fun getFormattedAmount(amount: String): String? {
        if (TextUtils.isEmpty(amount)) return amount
        try {
            val formatter = DecimalFormat("###,###,###,##0.00")
            return formatter.format(java.lang.Float.parseFloat(amount).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
            return amount
        }

    }

    fun getFormattedAmount(amount: Double): String {
        try {
            val formatter = DecimalFormat("###,###,###,##0.00")
            return formatter.format(amount)
        } catch (e: Exception) {
            e.printStackTrace()
            return amount.toString() + ""
        }

    }

    fun getStringFromBitmap(bitmap: Bitmap?): String? {
        if (bitmap == null) return null
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveAndShareQR(view: View) {
        //create bitmap from view
        view.isDrawingCacheEnabled = true
        val bitmap = view.drawingCache

        //save file from bitmap
        val imagePath = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/cashbaba_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".png"
        ) ////File imagePath
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(imagePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            //share file
            val context = view.context
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + "" + ".fileprovider",
                imagePath
            )
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "image/*"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)

            context.startActivity(Intent.createChooser(sharingIntent, "Share via"))
        } catch (e: FileNotFoundException) {
            Log.e("GREC", e.message, e)
        } catch (e: IOException) {
            Log.e("GREC", e.message, e)
        }

    }


    /**
     * Method checks if the app is in background or not
     */
    /*fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo.packageName == context.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }*/

    fun getTimeMilliSec(timeStamp: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = format.parse(timeStamp)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    fun validateName(name: String): Boolean {
        val pattern = Pattern.compile("^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z.\\s]{0,}$")
        val matcher = pattern.matcher(name)
        return matcher.matches()
    }

    fun specialCharacter(name: String): Boolean {
        val pattern = Pattern.compile("[a-zA-Z0-9.? ]*")
        val matcher = pattern.matcher(name)
        return matcher.matches()
    }

    fun showImageDialog(layoutInflater: LayoutInflater, bitmap: Bitmap) {
        /*val photoBinding = DataBindingUtil.inflate<PhotoDialogBinding>(layoutInflater, R.layout.dialog_photo_zoom, null, false)
        val context = layoutInflater.context
        val alertDialog = AlertDialog.Builder(context).setView(photoBinding.root).create()
        photoBinding.photoView.setImageBitmap(bitmap)
        alertDialog.show()*/

    }

    /*fun showImageDialog(layoutInflater: LayoutInflater, bitmap: Bitmap) {
        val photoBinding = DataBindingUtil.inflate<PhotoDialogBinding>(layoutInflater, R.layout.dialog_photo_zoom, null, false)
        val context = layoutInflater.context
        val alertDialog = AlertDialog.Builder(context).setView(photoBinding.root).create()
        photoBinding.photoView.setImageBitmap(bitmap)
        alertDialog.show()

    }

    fun removeCountryCodeFromNumber(context: Context, number: String): String? {
        var number = number
        if (TextUtils.isEmpty(number)) return number
        var countryCode = PrefData.getInstance(context).getString(PrefData.KEY_COUNTRY_CODE, "")
        if (number.startsWith(countryCode!!)) {
            number = number.substring(countryCode.length)
        } else if (countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1)
            if (number.startsWith(countryCode)) {
                number = number.substring(countryCode.length)
            }

        } else if (number.startsWith("+")) {
            number = number.substring(1)
            if (number.startsWith(countryCode)) {
                number = number.substring(countryCode.length)
            }

        }
        return number
    }*/

    fun isConnectionOn(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as
                    ConnectivityManager

        return if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.M
        ) {
            postAndroidMInternetCheck(connectivityManager)
        } else {
            preAndroidMInternetCheck(connectivityManager)
        }
    }

    private fun preAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null) {
            return (activeNetwork.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager
    ): Boolean {
        val network = connectivityManager.activeNetwork
        val connection =
            connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun getQueryMap(query: String): Map<String, String> {
        val params = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val map = HashMap<String, String>()
        for (param in params) {
            val parts = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (parts.size > 1) {
                val name = parts[0]
                val value = parts[1]
                map[name] = value
            }
        }
        return map
    }

    fun toTitleCase(str: String?): String {
        if (str == null) {
            return ""
        }

        var space = true
        val builder = StringBuilder(str)
        val len = builder.length

        for (i in 0 until len) {
            val c = builder[i]
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c))
                    space = false
                }
            } else if (Character.isWhitespace(c)) {
                space = true
            } else {
                builder.setCharAt(i, Character.toLowerCase(c))
            }
        }

        return builder.toString()
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.length == 0) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first) + s.substring(1)
        }
    }


}
