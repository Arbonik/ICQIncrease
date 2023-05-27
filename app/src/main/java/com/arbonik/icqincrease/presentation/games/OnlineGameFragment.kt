package com.arbonik.icqincrease.presentation.games

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arbonik.icqincrease.databinding.FragmentOnlineGameBinding
import com.arbonik.icqincrease.network.Global
import com.mpmep.classes.GameStatus
import com.mpmep.plugins.core.ExampleState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class OnlineGameFragment : Fragment() {

    private lateinit var binding: FragmentOnlineGameBinding

    private val viewModel: OnlineGameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlineGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        viewModel.connectToRoom(Global.gender, Global.age)
    }

    private fun initListeners() {
        binding.skip.setOnClickListener {
            viewModel.skipAnswer()
        }

        binding.inputText.doOnTextChanged { text, start, before, count ->
            val answer = text.toString().toIntOrNull()
            if (answer != null) {
                viewModel.sendAnswer(answer)
            }
        }

        viewModel.sharedFlowIn.onEach { serverResponse ->
            when (serverResponse.gameStatus) {
                GameStatus.EMPTY -> {
                    if (serverResponse.example is ExampleState.Example) {
                        binding.progressInfo.text = "Верно!"
                        binding.example.text = serverResponse.example.toString()
                        binding.progress.isVisible = false
                        binding.inputText.text?.clear()
                        binding.inputText.requestFocus()
                        val showKeyboard =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        showKeyboard.showSoftInput(binding.inputText, InputMethodManager.SHOW_IMPLICIT)
                        binding.inputText.requestFocus()
                        binding.youCounter.text = (serverResponse.score ?: -1).toString()
                    }
                }

                GameStatus.READY -> {
                    binding.progress.visibility = View.GONE
                }

                GameStatus.FALSE -> {
                    binding.progressInfo.text = "Пока ответ не верный :с"
                }

                GameStatus.GOT_NEW_EXAMPLE -> {

                    binding.enemyCounter.text = (serverResponse.score ?: -1).toString()
                    binding.enemyCounter
                }

                GameStatus.FINISH -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.progressInfo.text = "Ждем пока закончит другой игрок"
                    binding.inputText.isEnabled = false
                    binding.skip.isEnabled = false
                }

                GameStatus.WIN -> {
                    binding.progress.visibility = View.GONE
                    binding.progressInfo.visibility = View.GONE
                    AlertDialog.Builder(requireContext())
                        .setTitle("Вы выиграли")
                        .setOnDismissListener {
                            parentFragmentManager.popBackStack()
                        }
                        .setPositiveButton("Ура!") { dialog, _ ->
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
                        .setPositiveButton(":с") { dialog, _ ->
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

    companion object {
        @JvmStatic
        fun newInstance() = OnlineGameFragment()
    }
}