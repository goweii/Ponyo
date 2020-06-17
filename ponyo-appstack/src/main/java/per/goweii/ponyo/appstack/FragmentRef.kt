package per.goweii.ponyo.appstack

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

class FragmentRef(
    fragment: Fragment,
    val fragmentStack: FragmentStack
) : WeakReference<Fragment>(fragment) {
    companion object {
        fun from(fragment: Fragment): FragmentRef {
            val fragmentStack = FragmentStack.create(fragment.childFragmentManager)
            return FragmentRef(fragment, fragmentStack)
        }
    }
}