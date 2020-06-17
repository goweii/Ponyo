package per.goweii.ponyo.leak

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

internal class FragmentXWatcher {

    private val fragmentLifecycle = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentAttached(fm: FragmentManager, fragment: Fragment, context: Context) {
            super.onFragmentAttached(fm, fragment, context)
            fragment.childFragmentManager.registerFragmentLifecycleCallbacks(this, false)
        }

        override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
            super.onFragmentDetached(fm, fragment)
            LeakWatcher.watch(fragment)
        }
    }

    fun watch(activity: FragmentActivity) {
        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            fragmentLifecycle,
            false
        )
    }
}