package per.goweii.ponyo.appstack

import android.app.Fragment
import java.lang.ref.WeakReference

data class FragmentRef(
    val fragment: Fragment,
    val fragmentStack: FragmentStack
) : WeakReference<Fragment>(fragment) {
    companion object {
        fun from(fragment: Fragment): FragmentRef {
            val fragmentStack = FragmentStack.create(fragment.childFragmentManager)
            return FragmentRef(fragment, fragmentStack)
        }
    }
}