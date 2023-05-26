package com.arbonik.icqincrease.presentation.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arbonik.icqincrease.R
import com.arbonik.icqincrease.databinding.FragmentGameBinding
import com.arbonik.icqincrease.presentation.view_pager.RealizationViewPager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewPager: RealizationViewPager<Game1Adapter.ViewHolder>
    private var currentPos = 0
    private val viewModel: Game1ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = RealizationViewPager(
            binding.viewPager,
            viewModel.adapter,
            ::initViewsOnCurrentPage,
            ::onPageSelected
        )
        initListeners()
    }

    private fun initListeners() {
        viewModel.nextStateFlow.onEach {
            viewPager.jumpOnPageViewPager(currentPos++)
        }.launchIn(lifecycleScope)
    }

    private fun onPageSelected(pos: Int) {
        val holder = viewModel.adapter.getViewHolderByPosition(pos)
        holder?.let {
            focusInputAndAppearanceKeyboard(it.binding.result)
        }
    }

    private fun initViewsOnCurrentPage(pos: Int, holder: Game1Adapter.ViewHolder) {
        with(holder.binding) {
            skipButton.setOnClickListener {
                viewPager.jumpOnPageViewPager(pos + 1)
                viewModel.skip()
            }

            result.setOnKeyListener { v, keyCode, event ->
                val text = result.text.toString()
                if(text.length >= 9 && keyCode != 67){
                    return@setOnKeyListener true
                }
                if (text.isNotBlank() && text != "-") {
                    result.backgroundTintList = resources.getColorStateList(R.color.red)
                    viewModel.checkResult(text.toInt())
                } else {
                    result.backgroundTintList =
                        resources.getColorStateList(androidx.appcompat.R.color.material_blue_grey_800)
                }
                false
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