package com.rtchubs.restohubs.util

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.rtchubs.restohubs.util.AppConstants.COUNTRY_CODE

/**
 * Created by Priyanka on 10/5/17.
 */
class PrefixedEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyle) {
    /*if (TextUtils.isEmpty(prefix)) {
            setCompoundDrawables(null, null, null, null);
        } else
            setCompoundDrawables(new TextDrawable(prefix), null, null, null);*/ var prefix =
        COUNTRY_CODE
    private val mPrefixRect = Rect() // actual prefix size

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!TextUtils.isEmpty(prefix)) {
            paint.getTextBounds(prefix, 0, prefix.length, mPrefixRect)
            mPrefixRect.right += paint.measureText(" ").toInt() // add some offset
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!TextUtils.isEmpty(prefix)) canvas.drawText(
            prefix,
            super.getCompoundPaddingLeft().toFloat(),
            baseline.toFloat(),
            paint
        )
    }

    override fun getCompoundPaddingLeft(): Int {
        return if (!TextUtils.isEmpty(prefix)) super.getCompoundPaddingLeft() + mPrefixRect.width() else super.getCompoundPaddingLeft()
    }
}