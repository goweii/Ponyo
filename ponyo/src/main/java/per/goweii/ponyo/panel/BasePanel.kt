package per.goweii.ponyo.panel

import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.PanelProvider
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.log.LogManager

abstract class BasePanel : IPanel {

    private lateinit var view: View

    override fun createPanelView(container: FrameLayout): View {
        view = LayoutInflater.from(container.context).inflate(getPanelLayoutRes(), container, false)
        onPanelViewCreated(view)
        return view
    }

    override fun createPanelTab(container: LinearLayout): View {
        return TextView(container.context).apply {
            text = getPanelName()
            gravity = Gravity.CENTER
            val size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10F,
                container.context.resources.displayMetrics
            ).toInt()
            setPadding(size, size, size, size)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
            setTextColor(Color.WHITE)
        }
    }

    abstract fun getPanelLayoutRes(): Int
    abstract fun getPanelName(): String

    abstract fun onPanelViewCreated(view: View)
}