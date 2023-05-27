package com.arbonik.icqincrease.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.arbonik.icqincrease.R
import com.arbonik.icqincrease.databinding.FragmentRegistrationBinding
import com.arbonik.icqincrease.navigator
import com.arbonik.icqincrease.presentation.games.OnlineGameViewModel


class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val viewModel: RegistrationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonRegistration.setOnClickListener {
            if(binding.age.text.isBlank()){
                return@setOnClickListener
            }
            viewModel.saveData(binding.age.text.toString().toInt(), binding.radio1.isChecked)
            navigator().showMenuFragment()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegistrationFragment()
    }
}