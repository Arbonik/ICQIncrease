package com.arbonik.icqincrease.presentation.view_pager

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @param scrollElementBinding - опционально, если нужен seekBar
 */
class RealizationViewPager<T : RecyclerView.ViewHolder?>(
    private val viewPager: ViewPager2,
    private val adapterViewPager: AdapterViewPager<T>,
    private val initCurrentPage: (pos: Int, holder: T) -> Unit,
    private val onPageSelected: (pos: Int) -> Unit
) {

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        /**
         * 1. Существует бага ViewPager2 - при открытии клавиатуры иногда вызывается onPageSelected
         * решил проблему сохранением предыдущей позиции
         *
         * 2. findViewHolderForAdapterPosition не безопасен, по этому получаю холдер напрямую из адаптера, преждевременно добавив в кэш.
         * Но проблема не в этом. При пролистывании через seekBar адаптер не успевает сбилдить страницы, на это у него уходит обычно 160млс
         *
         */
        private var prevPosition = -1
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            if (position != prevPosition) {
                prevPosition = position

                viewPager.postDelayed({
                    startInitCurrentPage(position)
                }, 0)

            }
            onPageSelected.invoke(position)
        }
    }

    init {
        viewPager.adapter = adapterViewPager
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun startInitCurrentPage(position: Int) {
        val currentHolder = adapterViewPager.getViewHolderByPosition(position)  //Сначала убеждаемся что холдер сбилдился
        if (currentHolder == null) {
            //Для этого случая есть корутины, но мы пока без библиотек справляемся :D
            viewPager.postDelayed({ startInitCurrentPage(position) }, 20)
            return
        }
        initCurrentPage.invoke(position, currentHolder)
    }

    fun jumpOnPageViewPager(numberPage: Int) {
        viewPager.post {
            viewPager.setCurrentItem(numberPage, true)
        }
    }
}