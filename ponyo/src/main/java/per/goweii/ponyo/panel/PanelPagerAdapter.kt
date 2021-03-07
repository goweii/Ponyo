package per.goweii.ponyo.panel

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class PanelPagerAdapter : PagerAdapter() {
    private val panels = arrayListOf<Panel>()

    fun setPanels(data: List<Panel>) {
        panels.clear()
        panels.addAll(data)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = panels.size

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val panel = panels[position]
        val view = panel.createView(container)
        container.addView(view)
        return view
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
        super.setPrimaryItem(container, position, any)
        panels.forEachIndexed { index, panel -> panel.dispatchVisibleChanged(index == position) }
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val panel = panels[position]
        val view = any as View
        container.removeView(view)
        panel.destroyView()
    }

    override fun getPageTitle(position: Int): CharSequence {
        val panel = panels[position]
        return panel.getPanelName()
    }
}