package per.goweii.ponyo.appstack

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
interface OnAppStateChangeListener {
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
}