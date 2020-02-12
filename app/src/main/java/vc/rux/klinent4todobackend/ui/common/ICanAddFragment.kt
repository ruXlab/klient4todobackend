package vc.rux.klinent4todobackend.ui.common

import androidx.fragment.app.Fragment

interface ICanAddFragment {
    fun addFragment(fragment: Fragment, fragmentId: String = fragment.javaClass.name)
    fun replaceFragment(fragment: Fragment, fragmentId: String = fragment.javaClass.name)
}
