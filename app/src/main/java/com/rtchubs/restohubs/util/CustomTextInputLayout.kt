package com.rtchubs.restohubs.util
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.rtchubs.restohubs.R
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class CustomTextInputLayout @JvmOverloads constructor(context: Context,
                                                      attrs: AttributeSet? = null,
                                                      defStyleAttr: Int = 0)
    : TextInputLayout(context, attrs, defStyleAttr) {
    private var collapsingTextHelper: Any? = null
    private var bounds: Rect? = null
    private var recalculateMethod: Method? = null
    private val mainHintTextSize: Float
    private var editTextSize: Float = 0.toFloat()

    init {

        init()
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout)

        mainHintTextSize = a.getDimensionPixelSize(R.styleable.CustomTextInputLayout_mainHintTextSize, 0).toFloat()

        a.recycle()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        adjustBounds()
    }

    private fun init() {
        try {
            val cthField = TextInputLayout::class.java.getDeclaredField("mCollapsingTextHelper")
            cthField.isAccessible = true
            collapsingTextHelper = cthField.get(this)

            val boundsField = collapsingTextHelper!!.javaClass.getDeclaredField("mCollapsedBounds")
            boundsField.isAccessible = true
            bounds = boundsField.get(collapsingTextHelper) as Rect

            recalculateMethod = collapsingTextHelper!!.javaClass.getDeclaredMethod("recalculate")
        } catch (e: Exception) {
            collapsingTextHelper = null
            bounds = null
            recalculateMethod = null
            e.printStackTrace()
        }

    }

    private fun adjustBounds() {
        if (collapsingTextHelper == null) {
            return
        }

        try {
            bounds?.left = editText!!.left + editText!!.paddingLeft
            recalculateMethod?.invoke(collapsingTextHelper)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val b = child is EditText && mainHintTextSize > 0

        if (b) {
            val e = child as EditText
            editTextSize = e.textSize
            e.setTextSize(TypedValue.COMPLEX_UNIT_PX, mainHintTextSize)
        }

        super.addView(child, index, params)

        if (b) {
            editText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, editTextSize)
        }
    }
    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)

        if (!enabled) {
            return
        }

        try {
            val layout = this;
            val errorView : TextView = ((this.getChildAt(1) as ViewGroup).getChildAt(0) as ViewGroup).getChildAt(0) as TextView

            (layout.getChildAt(1) as ViewGroup).layoutParams.width = LayoutParams.WRAP_CONTENT
            (layout.getChildAt(1) as ViewGroup).getChildAt(0).layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}