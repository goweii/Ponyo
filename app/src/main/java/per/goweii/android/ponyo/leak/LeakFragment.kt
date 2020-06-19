package per.goweii.android.ponyo.leak

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_acti_stack.*
import per.goweii.android.ponyo.R
import kotlin.random.Random

object LeakFragmentHolder {
    val leakFragments = arrayListOf<Fragment>()
}

class LeakFragment: Fragment() {

    companion object {
        val leakFragments = arrayListOf<Fragment>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_acti_stack, container, false)
    }

    private var actiStackFragments = arrayListOf<LeakFragment>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)))
        tv_add.setOnClickListener {
            val leakFragment = LeakFragment()
            actiStackFragments.add(leakFragment)
            childFragmentManager.apply {
                beginTransaction().apply {
                    add(R.id.fl_container, leakFragment).show(leakFragment)
                }.commit()
            }
        }
        tv_remove.setOnClickListener {
            if (actiStackFragments.isEmpty()) {
                return@setOnClickListener
            }
            val actiStackFragment = actiStackFragments.last()
            actiStackFragments.remove(actiStackFragment)
            childFragmentManager.apply {
                beginTransaction().apply {
                    remove(actiStackFragment)
                }.commit()
            }
        }
        leakFragments.add(this)
        LeakFragmentHolder.leakFragments.add(this)
    }

}