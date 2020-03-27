package per.goweii.ponyo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_acti_stack.*
import kotlin.random.Random

class ActiStackFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_acti_stack, container, false)
    }

    private var actiStackFragments = arrayListOf<ActiStackFragment>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(Color.rgb(Random.nextInt(255), Random.nextInt(255), Random.nextInt(255)))
        tv_add.setOnClickListener {
            val actiStackFragment = ActiStackFragment()
            actiStackFragments.add(actiStackFragment)
            childFragmentManager.apply {
                beginTransaction().apply {
                    add(R.id.fl_container, actiStackFragment).show(actiStackFragment)
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
    }
}