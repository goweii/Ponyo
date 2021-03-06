package per.goweii.ponyo.panel

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class PanelPagerAdapter(
    private val panels: List<Panel>
) : PagerAdapter() {

    override fun getCount(): Int = panels.size

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view === any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val panel = panels[position]
        val view = panel.createView(container)
        container.addView(view)
        panel.dispatchViewCreated()
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val panel = panels[position]
        val view = panel.view
        container.removeView(view)
        panel.dispatchDestroyView()
    }

    override fun getPageTitle(position: Int): CharSequence {
        val panel = panels[position]
        return panel.getPanelName()
    }
}