package com.arbonik.icqincrease.presentation.neuroTesting

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.arbonik.icqincrease.databinding.FragmentOnlineGameBinding
import com.mpmep.plugins.core.ExampleState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class NeuroFragment : Fragment() {

    val viewModel : NeuroViewModel by viewModels()

    lateinit var binding : FragmentOnlineGameBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlineGameBinding.inflate(inflater, container, false)
        binding.youCounter.visibility = View.INVISIBLE
        binding.textView.text = "Возраст мозга: "
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    var lastTime = System.currentTimeMillis()
    private fun initListeners() {
        viewModel.currentExample.onEach { example ->
            if (example is ExampleState.Example) {
                binding.example.text = example.toString()
                binding.inputText.text?.clear()
                binding.inputText.requestFocus()
                val showKeyboard =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                showKeyboard.showSoftInput(binding.inputText, InputMethodManager.SHOW_IMPLICIT)
                binding.inputText.requestFocus()
            }
            if (example is ExampleState.Example){
                AlertDialog.Builder(requireContext())
                    .setTitle("Возраст вашего мозга:")
                    .setMessage("${viewModel.currentState.value}")
                    .setOnDismissListener {
                        parentFragmentManager.popBackStack()
                    }
                    .setPositiveButton("Круто!") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }.launchIn(lifecycleScope)

        viewModel.currentState.onEach {
            binding.enemyCounter.text = it.toString()
        }.launchIn(lifecycleScope)

        binding.skip.setOnClickListener {
            viewModel.skip()
        }

        binding.inputText.doOnTextChanged { text, start, before, count ->
            val answer = text.toString().toIntOrNull()
            if (answer != null) {
                val currentExample = viewModel.currentExample.value as? ExampleState.Example
                if (viewModel.answer(answer)){
                    if (currentExample != null) {
                        viewModel.predict(currentExample, lastTime)
                        lastTime = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = NeuroFragment()
    }
}