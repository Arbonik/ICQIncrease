package com.arbonik.icqincrease.presentation.games

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
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
import java.util.Timer
import java.util.TimerTask


class OnlineGameFragment : Fragment() {

    private lateinit var binding: FragmentOnlineGameBinding

    private val viewModel: OnlineGameViewModel by viewModels()
    private val myTimer: Timer = Timer()

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

    private var currentTime = 0
    private fun initListeners() {
        myTimer.schedule(object : TimerTask() {
            override fun run() {
                currentTime++
                binding.expectation.currentTime.text =
                    "${currentTime / 60}:${
                        if (currentTime % 60 < 10) {
                            "0"
                        } else {
                            ""
                        }
                    }${currentTime % 60}"
            }
        }, 0, 1000)
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
                        if (binding.youCounter.text != "0") {
                            binding.progressInfo.text = "Верно!"
                        } else {
                            binding.progressInfo.text = "Удачи!"
                            binding.cardView.isInvisible = false
                            (binding.expectation.root as View).isVisible = false
                        }
                        binding.example.text = serverResponse.example.toString()
                        binding.progress.isInvisible = true
                        binding.inputText.text?.clear()
                        binding.inputText.requestFocus()
                        val showKeyboard =
                            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        showKeyboard.showSoftInput(
                            binding.inputText,
                            InputMethodManager.SHOW_IMPLICIT
                        )
                        binding.inputText.requestFocus()
                        binding.youCounter.text = (serverResponse.score ?: -1).toString()
                    }
                }

                GameStatus.READY -> {
                    binding.progress.isInvisible = true
                }

                GameStatus.FALSE -> {
                    binding.progressInfo.text = "Пока ответ не верный :с"
                }

                GameStatus.GOT_NEW_EXAMPLE -> {
                    binding.enemyCounter.text = (serverResponse.score ?: -1).toString()
                    binding.enemyCounter
                }

                GameStatus.FINISH -> {
//                    binding.progress.visibility = View.VISIBLE
                    binding.progressInfo.text = "Ждем пока закончит другой игрок"
                    binding.inputText.isEnabled = false
                    binding.inputText.isVisible =false
                    binding.example.isVisible = false
                    binding.skip.isEnabled = false
                }

                GameStatus.WIN -> {
                    binding.progress.isInvisible = true
                    binding.progressInfo.isVisible = true
                    binding.cardView.isVisible = false
                    binding.boardPositiveResult.isVisible = true
                }

                GameStatus.LOSE -> {
                    binding.progress.isInvisible = true
                    binding.progressInfo.visibility = View.GONE
                    binding.cardView.isVisible = false
                    binding.boardNegativeResult.isVisible = true
                }

                GameStatus.SHUTDOWN -> {
                    parentFragmentManager.popBackStack()
                }

                GameStatus.AWAIT -> {
                    binding.cardView.isInvisible = true
                    binding.progress.isInvisible = false
                    binding.progressInfo.visibility = View.VISIBLE
                    binding.progressInfo.text = "Ждем подключение игрока"
                }
                GameStatus.TIMEOUT -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Не удалось найти игру, попробуйте снова :)")
                        .setOnDismissListener {
                            parentFragmentManager.popBackStack()
                        }
                        .setPositiveButton("Ок"){ al, a ->
                            al.dismiss()
                        }
                }
            }
        }.launchIn(lifecycleScope)
    }

    companion object {
        @JvmStatic
        fun newInstance() = OnlineGameFragment()
    }
}