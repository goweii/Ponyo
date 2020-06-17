package per.goweii.ponyo.leak

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
internal class FragmentOWatcher {

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

    fun watch(activity: Activity) {
        activity.fragmentManager.registerFragmentLifecycleCallbacks(
            fragmentLifecycle,
            false
        )
    }
}