package com.rtchubs.restohubs.util

import android.app.Activity
import android.app.ActivityManager
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import com.rtchubs.restohubs.event.ErrorMessageEvent
import com.rtchubs.restohubs.event.LoadingEvent
import com.rtchubs.restohubs.event.RefreshTokenExpireEvent
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONException
import java.io.*
import java.nio.charset.Charset
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


object CommonUtils {

    // id to handle the notification in the notification tray
    internal val NOTIFICATION_ID = 10001
    internal val NOTIFICATION_ID_BIG_IMAGE = 101
    internal val PUSH_NOTIFICATION = "pushNotification"
    internal val DATABASE_CHANGED = "com.securepenny.cashbaba.DATABASE_CHANGED"

    @Throws(IOException::class)
    fun loadJSONFromAsset(context: Context, jsonFileName: String): String {
        val manager = context.assets
        val `is` = manager.open(jsonFileName)

        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()

        return String(buffer, Charset.forName("UTF-8"))
    }

    fun getJSONArray(context: Context, jsonFileName: String): JSONArray {
        var bankJsonArray = JSONArray()
        try {
            bankJsonArray = JSONArray(
                loadJSONFromAsset(context, jsonFileName)
            )
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return bankJsonArray
    }

    fun getJSONValueByFieldName(
        searchValueField: String,
        searchValue: String?,
        returnValueFieldName: String,
        jsonArray: JSONArray
    ): String {
        var bankName: String? = null
        try {

            for (n in 0 until jsonArray.length()) {
                val bankJsonObj = jsonArray.getJSONObject(n)
                if (bankJsonObj.get(searchValueField) == searchValue) {
                    bankName = bankJsonObj.get(returnValueFieldName).toString()
                    break
                }
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return if (bankName != null) {
            bankName
        } else {
            ""
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
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }


    fun getFormattedMobile(mobileNumber: String?): String {
        if (mobileNumber == null) return ""
        try {
            return mobileNumber.replace("(?<=\\w{5})\\w(?=\\w{3})".toRegex(), "*")
        } catch (e: Exception) {
            return mobileNumber
        }

    }

    fun getFormattedEmail(email: String?): String {
        if (email == null) return ""
        try {
            return email.replace("(?<=.{3}).(?=.*@)".toRegex(), "*")
        } catch (e: Exception) {
            return email
        }

    }

    fun getFormattedAmount(amount: String): String? {
        if (TextUtils.isEmpty(amount)) return amount
        try {
            return getFormattedAmount(amount.toDouble())
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

    fun getStringFromBitmap(bitmap: Bitmap): String {
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
            Environment.getExternalStorageDirectory().toString() + "/cashbaba_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(
                Date()
            ) + ".png"
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
    fun isAppIsInBackground(context: Context): Boolean {
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
            if (componentInfo?.packageName == context.packageName) {
                isInBackground = false
            }
        }

        return isInBackground
    }

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

    fun showImageDialog(layoutInflater: LayoutInflater, url: String, placeHolder: Int) {
        /*  PhotoDialogBinding photoBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_photo_zoom, null, false);
        Context context = layoutInflater.getContext();
        AlertDialog alertDialog = new AlertDialog.Builder(context).setView(photoBinding.getRoot()).create();
        Picasso.get().load(url).placeholder(placeHolder).error(R.drawable.error_photo).into(photoBinding.photoView);
        alertDialog.show();*/

    }

    fun expand(v: View) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight

        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun loadJSONFromAsset(activity: Activity, filename: String): String? {
        var json: String? = null
        try {
            val `is` = activity.assets.open(filename)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
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

    fun getCardType(cardNumber: String?): String? {
        if (cardNumber.isNullOrBlank()) return null
        return if (cardNumber.startsWith("4")) {
            "Visa"
        } else if (cardNumber.startsWith("5")) {
            "MasterCard"
        } else if (cardNumber.startsWith("6")) {
            "Discover"
        } else if (cardNumber.startsWith("35")) {
            "JCB"
        } else if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            "AMEX"
        } else {
            null
        }
    }

    fun fireErrorMessageEvent(error: String?, showInAlert: Boolean = true) {
        error?.let { msg ->
            EventBus.getDefault().post(ErrorMessageEvent(msg, showInAlert))
        }
    }

    fun fireLoadingEvent(showProgress: Boolean) {
        EventBus.getDefault().post(LoadingEvent(showProgress))
    }


    /*fun showSnack(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        gravity: Int = Gravity.BOTTOM,
        snackType: SNACK_TYPE? = null
    ) {
        val snackbar = Snackbar.make(view, message, duration)
        val snackView = snackbar.view

        *//*color snackbar based on success or error type*//*
        if (snackType != null) {
            snackView.setBackgroundColor(
                ContextCompat.getColor(
                    view.context,
                    if (snackType == SNACK_TYPE.SUCCESS) R.color.colorGreen
                    else R.color.colorRed
                )
            )
        }

        *//*if duration is infinite show a close button*//*
        if (duration == Snackbar.LENGTH_INDEFINITE) {
            snackbar.setActionTextColor(
                ContextCompat
                    .getColor(
                        view.context,
                        R.color.black
                    )
            )
            snackbar.setAction(R.string.close) { }
        }
        *//*set gravity to snackbar. if gravity is bottom no need to set explicitly*//*
        if (gravity != Gravity.BOTTOM) {
            val params = snackView.layoutParams as? CoordinatorLayout.LayoutParams
            params?.let {
                it.gravity = gravity
                if (gravity == Gravity.TOP) {
                    // calculate actionbar height to show snackbar below toolbar
                    val tv = TypedValue()
                    val context = view.context
                    if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                        val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                            tv.data,
                            context.resources.displayMetrics
                        )
                        it.setMargins(0, actionBarHeight, 0, 0)
                    }
                }

                snackView.layoutParams = it
            }
        }

        val textView =
            snackView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.isSingleLine = false  // show multiple line
        textView.maxLines = 5  // show multiple line

        snackbar.show()
    }*/

    /*fun showSnack(
        view: View, @StringRes id: Int,
        duration: Int = Snackbar.LENGTH_LONG,
        gravity: Int = Gravity.BOTTOM,
        snackType: SNACK_TYPE? = null
    ) {
        showSnack(
            view = view,
            message = view.context.getString(id),
            duration = duration,
            gravity = gravity,
            snackType = snackType
        )
    }*/


/*    fun showErrorSnack(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        gravity: Int = Gravity.BOTTOM
    ) {
        showSnack(
            view = view,
            message = message,
            duration = duration,
            gravity = gravity,
            snackType = SNACK_TYPE.ERROR
        )
    }*/


    /*fun showErrorSnack(
        view: View,
        @StringRes id: Int,
        duration: Int = Snackbar.LENGTH_LONG,
        gravity: Int = Gravity.BOTTOM
    ) {
        showSnack(
            view = view,
            id = id,
            duration = duration,
            gravity = gravity,
            snackType = SNACK_TYPE.ERROR
        )
    }*/

    /*fun showSuccessSnack(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        gravity: Int = Gravity.BOTTOM
    ) {
        showSnack(
            view = view,
            message = message,
            duration = duration,
            gravity = gravity,
            snackType = SNACK_TYPE.SUCCESS
        )
    }

    fun showSuccessSnack(
        view: View,
        @StringRes id: Int,
        duration: Int = Snackbar.LENGTH_LONG,
        gravity: Int = Gravity.BOTTOM
    ) {
        showSnack(
            view = view,
            id = id,
            duration = duration,
            gravity = gravity,
            snackType = SNACK_TYPE.SUCCESS
        )
    }*/

    /**
     * Indicate whether this device can authenticate the user with biometrics
     * @return true if there are any available biometric sensors and biometrics are enrolled on the device, if not, return false
     */
    fun canAuthenticateWithDeviceCredential(context: Context): Boolean {
        /* val biometricManager = BiometricManager.from(context)
         when (biometricManager.canAuthenticate()) {
             BiometricManager.BIOMETRIC_SUCCESS -> {
                 Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                 return true
             }
             BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                 Log.e("MY_APP_TAG", "No biometric features available on this device.")
             BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                 Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
             BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                 Log.e("MY_APP_TAG", "The user hasn't associated " +
                         "any biometric credentials with their account.")
         }
         return false*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val km = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
            return km?.isKeyguardSecure == true
        }
        return false
    }

    /*  fun hasBiometricEnrolled(context: Context): Boolean {
          return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              val bm = BiometricManager.from(context)
              val canAuthenticate = bm.canAuthenticate()
              (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS)

          } else {
              false
          }
      }*/

    fun dismissKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(
            activity.findViewById<View>(android.R.id.content).windowToken,
            0
        )
    }

    fun stringToCardFormat(input: String): String? {
        val result = StringBuilder()
        val inpFirstSixDigit = input.substring(0, 6)
        val inpLastFourDigit = input.substring(input.length-4, input.length)
        val combineString = "$inpFirstSixDigit******$inpLastFourDigit"
        if (combineString.contains("*")) {
            for (i in combineString.indices) {
                if (i % 4 == 0 && i != 0) {
                    result.append(" ")
                }
                result.append(combineString[i])
            }
        }
        return result.toString().replace("*", "x")
    }

    fun sessionOut() {
        EventBus.getDefault().postSticky(RefreshTokenExpireEvent())
    }

    enum class SNACK_TYPE {
        ERROR, SUCCESS
    }

}// This utility class is not publicly instantiable
