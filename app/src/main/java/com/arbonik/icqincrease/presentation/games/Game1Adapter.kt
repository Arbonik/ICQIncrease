package com.arbonik.icqincrease.presentation.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.arbonik.icqincrease.databinding.Game1ExampleItemBinding
import com.arbonik.icqincrease.presentation.view_pager.AdapterViewPager
import com.mpmep.plugins.core.ExampleState

class Game1Adapter(
    private val examples: MutableList<ExampleState.Example>
) : AdapterViewPager<Game1Adapter.ViewHolder>() {

    fun addItem(item: ExampleState.Example) {
        examples.add(item)
//        notifyItemChanged(examples.lastIndex)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            Game1ExampleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            number1.text = examples[position].first.toString()
            number2.text = examples[position].second.toString()
            operator.text = examples[position].op.s
            result.text.clear()
            skipButton.isVisible = true
        }
        holderCache[position] = holder
    }

    override fun getItemCount(): Int = examples.size

    class ViewHolder(val binding: Game1ExampleItemBinding) : RecyclerView.ViewHolder(binding.root)
}