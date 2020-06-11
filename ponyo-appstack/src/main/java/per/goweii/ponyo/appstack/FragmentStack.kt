package per.goweii.ponyo.appstack

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

class FragmentStack : FragmentManager.FragmentLifecycleCallbacks() {
    internal var fragmentStackUpdateListener: (() -> Unit)? = null

    val fragmentInfos = arrayListOf<FragmentInfo>()
        get() {
            val iterator = field.iterator()
            while (iterator.hasNext()) {
                val fragmentInfo = iterator.next()
                if (fragmentInfo.fragmentRef.get() == null) {
                    iterator.remove()
                }
            }
            return field
        }

    override fun onFragmentAttached(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        context: Context
    ) {
        super.onFragmentAttached(fragmentManager, fragment, context)
        val fragmentStack = FragmentStack()
        fragmentStack.fragmentStackUpdateListener = {
            fragmentStackUpdateListener?.invoke()
        }
        fragment.childFragmentManager.registerFragmentLifecycleCallbacks(fragmentStack, false)
        val fragmentInfo = FragmentInfo(WeakReference(fragment), fragmentStack)
        fragmentInfos.add(fragmentInfo)
        fragmentStackUpdateListener?.invoke()
    }

    override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
        super.onFragmentDetached(fm, fragment)
        var fragmentInfo: FragmentInfo? = null
        for (i in fragmentInfos.size - 1 downTo 0) {
            val info = fragmentInfos[i]
            if (info.fragmentRef == fragment) {
                fragmentInfo = info
                break
            }
        }
        fragmentInfo ?: return
        fragmentInfos.remove(fragmentInfo)
        fragmentInfo.fragmentStack.fragmentStackUpdateListener = null
        fragment.childFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentInfo.fragmentStack)
        fragmentInfo.fragmentRef.clear()
        fragmentStackUpdateListener?.invoke()
    }
}