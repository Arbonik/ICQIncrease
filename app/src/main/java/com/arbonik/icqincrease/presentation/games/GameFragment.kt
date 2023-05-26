package com.arbonik.icqincrease.presentation.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.arbonik.icqincrease.databinding.FragmentGameBinding
import com.arbonik.icqincrease.presentation.view_pager.RealizationViewPager


class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewPager: RealizationViewPager<Game1Adapter.ViewHolder>
    private lateinit var adapter: Game1Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = Game1Adapter()
        viewPager = RealizationViewPager(
            binding.viewPager,
            adapter,
            ::initViewsOnCurrentPage,
            ::onPageSelected
        )
    }

    private fun onPageSelected(pos: Int) {
        val holder = adapter.getViewHolderByPosition(pos)
        holder?.let {
            focusInputAndAppearanceKeyboard(it.binding.result)
        }
    }

    private fun initViewsOnCurrentPage(pos: Int, holder: Game1Adapter.ViewHolder) {
        with(holder.binding) {
            skipButton.setOnClickListener {
                viewPager.jumpOnPageViewPager(pos + 1)
            }
        }
    }

    /** Фокусировка на ввод названия рецепта и автоматическое появление клавиатуры для ввода */
    private fun focusInputAndAppearanceKeyboard(editText: EditText) {
        if (editText.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GameFragment()
    }
}