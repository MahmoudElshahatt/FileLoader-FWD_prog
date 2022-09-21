package com.example.imageloader.utils


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.imageloader.R
import kotlin.properties.Delegates

//I made some changes to the CustomButton Class and tried to type my own branded code here.
class CustomButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var textColor = 0
    private var buttonBackgroundColor = 0
    private var CircleDownloadColor = 0
    private var loadingColor = 0

    private var TextString = ""
    private var progress = 0

    private val valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(1500)

    //Observing the state of the button.
    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                TextString = resources.getString(R.string.button_loading)
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                TextString = resources.getString(R.string.button_name)
                valueAnimator.cancel()
                progress = 0
            }
        }
        invalidate()
    }

    init {
        //getting the data from the xml layout attributes.
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            CircleDownloadColor = getColor(R.styleable.LoadingButton_buttonCircleColor, 0)
        }
        //Applying listener and some attributes to the ValueAnimator.
        valueAnimator.apply {
            addUpdateListener {
                progress = it.animatedValue as Int
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }
        //initial state.
        buttonState = ButtonState.Completed
    }

    //Setting up the text on the button.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 85.0f
        typeface = Typeface.create("", Typeface.NORMAL)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //button background drawing.
        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
        //loading bar or rectangle drawing.
        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, widthSize * progress / 360f, heightSize.toFloat(), paint)
        //Circle drawing with color
        paint.color = CircleDownloadColor
        canvas?.drawArc(
            widthSize - 200f,
            40f,
            widthSize - 150f,
            100f,
            0f,
            progress.toFloat(),
            true,
            paint
        )
        //Text drawing.
        paint.color = textColor
        canvas?.drawText(TextString, widthSize / 2.0f, heightSize / 2.0f + 29.0f, paint)

    }

    //to measure the width and height .
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}