package per.goweii.ponyo.appstack

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

data class FragmentInfo(
    val fragmentRef: WeakReference<Fragment>,
    val fragmentStack: FragmentStack
)