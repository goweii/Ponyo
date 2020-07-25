package per.goweii.ponyo.startup

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

internal class FragmentStarter: FragmentManager.FragmentLifecycleCallbacks() {

    companion object {
        fun create(fragmentManager: FragmentManager): FragmentStarter {
            return FragmentStarter().also {
                fragmentManager.registerFragmentLifecycleCallbacks(it, false)
            }
        }
    }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        super.onFragmentAttached(fragmentManager, fragment, context)
        Starter.initializeFollowFragment(fragment.javaClass)
        create(fragment.childFragmentManager)
    }
}