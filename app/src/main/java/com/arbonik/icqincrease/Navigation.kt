package com.arbonik.icqincrease

import androidx.fragment.app.Fragment

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}
interface Navigator {
    fun showGameFragment()
    fun showStatisticsFragment()
    fun showMenuFragment()
}