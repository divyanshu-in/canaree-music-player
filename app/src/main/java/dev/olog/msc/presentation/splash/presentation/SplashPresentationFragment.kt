package dev.olog.msc.presentation.splash.presentation

import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.activity_splash.*

class SplashPresentationFragment : BaseFragment() {

    override fun onResume() {
        super.onResume()
        root.setOnClickListener { activity!!.viewPager.currentItem = 1 }
    }

    override fun onPause() {
        super.onPause()
        root.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_splash_presentation
}