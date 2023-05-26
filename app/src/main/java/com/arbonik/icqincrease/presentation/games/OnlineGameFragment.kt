package com.arbonik.icqincrease.presentation.games

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arbonik.icqincrease.R
import com.arbonik.icqincrease.core.ExampleState
import com.arbonik.icqincrease.databinding.FragmentGameBinding
import com.arbonik.icqincrease.presentation.view_pager.RealizationViewPager
import com.mpmep.classes.GameStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OnlineGameFragment :  Fragment(){

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewPager: RealizationViewPager<Game1Adapter.ViewHolder>
    private var currentPos = 0

    private val adapter: Game1Adapter by lazy {
        Game1Adapter(mutableListOf())
    }
    private val viewModel : OnlineGameViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.connectToRoom()

        viewPager = RealizationViewPager(
            binding.viewPager,
            adapter,
            ::initViewsOnCurrentPage,
            ::onPageSelected
        )
        initListeners()
    }

    private fun initListeners() {
        viewModel.sharedFlowIn.onEach { serverResponse->
            when (serverResponse.gameStatus){
                GameStatus.EMPTY -> {
                    if (serverResponse.example is ExampleState.Example)
                        adapter.addItem(serverResponse.example)
                    else
                        // TODO вопросы кончились
                    viewPager.jumpOnPageViewPager(currentPos++)
                }

                GameStatus.READY -> {
                    binding.progress.visibility = View.GONE
                    binding.progressInfo.visibility = View.GONE
                }
                GameStatus.FALSE -> {

                }
                GameStatus.GOT_NEW_EXAMPLE -> {
                    binding.progressInfo.text = "противник решил еще одну!"
                    binding.progressInfo.visibility = View.VISIBLE
                    delay(1000)
                    binding.progressInfo.visibility = View.GONE
                }
                GameStatus.FINISH -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.progressInfo.text = "ждем пока закончит другой игрок"
                    binding.progressInfo.visibility = View.VISIBLE
                }
                GameStatus.WIN -> {
                    binding.progress.visibility = View.GONE
                    binding.progressInfo.visibility = View.GONE
                    AlertDialog.Builder(requireContext())
                        .setTitle("Вы выиграли")
                        .setOnDismissListener {
                            parentFragmentManager.popBackStack()
                        }
                        .setPositiveButton ("Ура!"){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
                }
                GameStatus.LOSE -> {
                    binding.progress.visibility = View.GONE
                    binding.progressInfo.visibility = View.GONE

                    AlertDialog.Builder(requireContext())
                        .setTitle("Вы проиграли")
                        .setOnDismissListener {
                            parentFragmentManager.popBackStack()
                        }
                        .setPositiveButton (":с"){ dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
                GameStatus.SHUTDOWN -> {

                }
                GameStatus.AWAIT -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.progressInfo.visibility = View.VISIBLE
                    binding.progressInfo.text = "Ждем подключение игрока"
                }
            }
        }.launchIn(lifecycleScope)
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
                viewModel.skipAnswer()
                viewPager.jumpOnPageViewPager(pos + 1)
            }

            result.setOnKeyListener { v, keyCode, event ->
                val text = result.text.toString()
                if(text.length >= 9 && keyCode != 67){
                    return@setOnKeyListener true
                }
                if (text.isNotBlank() && text != "-") {
                    result.backgroundTintList = resources.getColorStateList(R.color.red)
                    viewModel.sendAnswer(text.toInt())
                } else {
                    result.backgroundTintList =
                        resources.getColorStateList(androidx.appcompat.R.color.material_blue_grey_800)
                }
                false
            }
        }
    }

    /** Фокусировка на ввод названия, автоматическое появление клавиатуры для ввода */
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