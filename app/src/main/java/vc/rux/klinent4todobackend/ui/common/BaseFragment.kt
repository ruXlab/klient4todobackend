package vc.rux.klinent4todobackend.ui.common

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment() {
    protected fun setToolbarTitles(title: String, subtitle: String = "") {
        (activity as AppCompatActivity?)?.supportActionBar?.let {
            it.title = title
            it.subtitle = subtitle
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setToolbarTitles("", "")
    }
}
