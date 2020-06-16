package per.goweii.ponyo.leak

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

internal data class FragmentInfo(
    val fragmentRef: WeakReference<Fragment>,
    val fragmentStack: FragmentStack,
    var destroyed: Boolean = false
)