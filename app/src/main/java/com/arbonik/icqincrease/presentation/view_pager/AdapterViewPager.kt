package com.arbonik.icqincrease.presentation.view_pager

import androidx.recyclerview.widget.RecyclerView

abstract class AdapterViewPager<T : RecyclerView.ViewHolder?> : RecyclerView.Adapter<T>() {
    protected val holderCache = mutableMapOf<Int, T>()

    /**
     * @return - адаптер должен уметь хэшировать созданные ViewHolder и возвращать по позиции
     * (В onBindViewHolder() в конце напишите: holderCache[position] = holder)
     */
    fun getViewHolderByPosition(pos: Int): T? = holderCache[pos]

}