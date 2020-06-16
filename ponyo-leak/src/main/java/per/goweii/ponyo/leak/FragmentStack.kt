package per.goweii.ponyo.leak

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

internal class FragmentStack : FragmentManager.FragmentLifecycleCallbacks() {

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
        fragment.childFragmentManager.registerFragmentLifecycleCallbacks(fragmentStack, false)
        val fragmentInfo = FragmentInfo(WeakReference(fragment), fragmentStack)
        fragmentInfos.add(fragmentInfo)
    }

    override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
        super.onFragmentDetached(fm, fragment)
        for (i in fragmentInfos.size - 1 downTo 0) {
            val info = fragmentInfos[i]
            if (info.fragmentRef.get() == fragment) {
                info.destroyed = true
                break
            }
        }
    }
}