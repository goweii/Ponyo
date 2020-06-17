package per.goweii.ponyo.appstack

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentStack private constructor() {
    private val fragmentLifecycle = FragmentLifecycle()

    internal var fragmentStackUpdateListener: (() -> Unit)? = null

    val fragments = arrayListOf<FragmentRef>()
        get() {
            val iterator = field.iterator()
            while (iterator.hasNext()) {
                val ref = iterator.next()
                if (ref.get() == null) {
                    iterator.remove()
                }
            }
            return field
        }

    companion object {
        internal fun create(manager: FragmentManager): FragmentStack {
            val fragmentStack = FragmentStack()
            manager.registerFragmentLifecycleCallbacks(fragmentStack.fragmentLifecycle, false)
            return fragmentStack
        }
    }

    fun recycle(manager: FragmentManager) {
        manager.unregisterFragmentLifecycleCallbacks(fragmentLifecycle)
    }

    private inner class FragmentLifecycle : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentAttached(
            fragmentManager: FragmentManager,
            fragment: Fragment,
            context: Context
        ) {
            super.onFragmentAttached(fragmentManager, fragment, context)
            val fragmentRef = FragmentRef.from(fragment)
            fragmentRef.fragmentStack.fragmentStackUpdateListener = {
                fragmentStackUpdateListener?.invoke()
            }
            fragments.add(fragmentRef)
            fragmentStackUpdateListener?.invoke()
        }

        override fun onFragmentDetached(fm: FragmentManager, fragment: Fragment) {
            super.onFragmentDetached(fm, fragment)
            var fragmentRef: FragmentRef? = null
            for (i in fragments.size - 1 downTo 0) {
                val ref = fragments[i]
                if (ref.get() == fragment) {
                    fragmentRef = ref
                    break
                }
            }
            fragmentRef?.let { ref ->
                fragments.remove(ref)
                ref.fragmentStack.fragmentStackUpdateListener = null
                ref.fragmentStack.recycle(fragment.childFragmentManager)
                ref.clear()
            }
            fragmentStackUpdateListener?.invoke()
        }
    }
}