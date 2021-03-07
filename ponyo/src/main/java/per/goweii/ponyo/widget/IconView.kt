package per.goweii.ponyo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import kotlin.math.max
import kotlin.math.min

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
@SuppressLint("AppCompatCustomView")
class IconView : ImageView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //val w = MeasureSpec.getSize(widthMeasureSpec)
        //val h = MeasureSpec.getSize(heightMeasureSpec)
        //val s = min(w, h)
        //setMeasuredDimension(s, s)
    }
}