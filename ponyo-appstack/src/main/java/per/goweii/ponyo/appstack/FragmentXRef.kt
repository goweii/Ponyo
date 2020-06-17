package per.goweii.ponyo.appstack

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

data class FragmentXRef(
    val fragment: Fragment,
    val fragmentStack: FragmentStack
) : WeakReference<Fragment>(fragment) {
    companion object {
        fun from(fragment: Fragment): FragmentXRef {
            val fragmentStack = FragmentStack.create(fragment.childFragmentManager)
            return FragmentXRef(fragment, fragmentStack)
        }
    }
}