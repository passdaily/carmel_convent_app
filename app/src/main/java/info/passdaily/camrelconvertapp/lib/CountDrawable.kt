package info.passdaily.camrelconvertapp.lib

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import info.passdaily.camrelconvertapp.R
import androidx.core.content.ContextCompat

class CountDrawable(context: Context) : Drawable() {
    private val mBadgePaint: Paint
    private val mTextPaint: Paint
    private val mTxtRect = Rect()
    private var mCount = ""
    private var mWillDraw = false
    override fun draw(canvas: Canvas) {
        if (!mWillDraw) {
            return
        }
        val bounds = bounds
        val width = (bounds.right - (bounds.left-4)).toFloat()
        val height = (bounds.bottom - bounds.top).toFloat()

        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */
        val radius = width.coerceAtLeast(height) / 2 / 2
        val centerX = width - radius - 1 + 5
        val centerY = radius - 5
        if (mCount.length <= 2) {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY, ((radius + 10.5) as Double).toFloat(), mBadgePaint)
        } else {
            canvas.drawCircle(centerX, centerY, ((radius + 9.5) as Double).toFloat(), mBadgePaint)
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = (mTxtRect.bottom - mTxtRect.top).toFloat()
        val textY = centerY + textHeight / 2f
//        if (mCount.length > 2) canvas.drawText(
//            "99+",
//            centerX,
//            textY,
//            mTextPaint
//        ) else
           canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: String) {
        mCount = count

        // Only draw a badge if there are notifications.
        mWillDraw = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    init {
        val mTextSize = context.resources.getDimension(R.dimen.text_size_07)
        mBadgePaint = Paint()
        mBadgePaint.color = ContextCompat.getColor(context.applicationContext, R.color.green_300)
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        mTextPaint.typeface = Typeface.DEFAULT
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }
}