package per.goweii.ponyo.panel

import android.content.Context
import android.util.TypedValue
import androidx.viewpager.widget.ViewPager
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import per.goweii.ponyo.R

class PanelNavigatorAdapter(
    private val pager: ViewPager
): CommonNavigatorAdapter() {
    private val panels = arrayListOf<Panel>()

    fun setPanels(data: List<Panel>) {
        panels.clear()
        panels.addAll(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return panels.size
    }

    override fun getTitleView(context: Context, index: Int): IPagerTitleView {
        val pagerTitleView = ColorTransitionPagerTitleView(context)
        pagerTitleView.normalColor = context.resources.getColor(R.color.ponyo_colorOnSurface)
        pagerTitleView.selectedColor = context.resources.getColor(R.color.ponyo_colorOnPrimary)
        pagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.ponyo_text_size_btn))
        pagerTitleView.text = panels[index].getPanelName()
        pagerTitleView.setOnClickListener { pager.currentItem = index }
        return pagerTitleView
    }

    override fun getIndicator(context: Context): IPagerIndicator {
        val pagerIndicator = WrapPagerIndicator(context)
        pagerIndicator.fillColor = context.resources.getColor(R.color.ponyo_colorPrimary)
        pagerIndicator.verticalPadding = context.resources.getDimension(R.dimen.ponyo_margin_thin).toInt()
        pagerIndicator.horizontalPadding = context.resources.getDimension(R.dimen.ponyo_margin_middle).toInt()
        return pagerIndicator
    }
}