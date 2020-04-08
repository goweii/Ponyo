package per.goweii.android.ponyo.log

class LogTest {

    init {
        test1()
        test2()
    }

    private fun test1(){
        i(null, "test1")
    }

    private fun test2(){
        LogUtils.d(null, "test1")
    }

}