package com.arbonik.icqincrease.presentation.games

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.arbonik.icqincrease.databinding.Game1ExampleItemBinding
import com.arbonik.icqincrease.presentation.view_pager.AdapterViewPager
import com.mpmep.plugins.core.ExampleState

class Game1Adapter(
    private val examples: MutableList<ExampleState.Example>
) : AdapterViewPager<Game1Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            Game1ExampleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            example.text = examples[position].toString()
            result.text?.clear()
            skipButton.isVisible = true
//            view.postDelayed({
                focusInputAndAppearanceKeyboard(result)
//            },300)
        }
        holderCache[position] = holder
    }

    /** Фокусировка на ввод названия рецепта и автоматическое появление клавиатуры для ввода */
    private fun focusInputAndAppearanceKeyboard(editText: EditText) {
        if (editText.requestFocus()) {
            val imm =
                editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    override fun getItemCount(): Int = examples.size

    class ViewHolder(val binding: Game1ExampleItemBinding) : RecyclerView.ViewHolder(binding.root)
}