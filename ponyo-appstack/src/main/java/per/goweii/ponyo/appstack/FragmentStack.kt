package per.goweii.ponyo.appstack

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentStack: FragmentManager.FragmentLifecycleCallbacks() {
    internal var fragmentStackUpdateListener: (() -> Unit)? = null

    val fragmentInfos = arrayListOf<FragmentInfo>()

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        val fragmentStack = FragmentStack()
        fragmentStack.fragmentStackUpdateListener = {
            fragmentStackUpdateListener?.invoke()
        }
        f.childFragmentManager.registerFragmentLifecycleCallbacks(fragmentStack, false)
        val fragmentInfo = FragmentInfo(f, fragmentStack)
        fragmentInfos.add(fragmentInfo)
        fragmentStackUpdateListener?.invoke()
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        var fragmentInfo: FragmentInfo? = null
        for (i in fragmentInfos.size - 1 downTo 0) {
            val info = fragmentInfos[i]
            if (info.fragment == f) {
                fragmentInfo = info
                break
            }
        }
        fragmentInfo ?: return
        fragmentInfos.remove(fragmentInfo)
        fragmentInfo.fragmentStack.fragmentStackUpdateListener = null
        fragmentInfo.fragment.childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentInfo.fragmentStack)
        fragmentStackUpdateListener?.invoke()
    }
}