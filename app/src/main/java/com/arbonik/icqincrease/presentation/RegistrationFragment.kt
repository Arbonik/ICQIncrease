package com.arbonik.icqincrease.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.arbonik.icqincrease.R
import com.arbonik.icqincrease.databinding.FragmentRegistrationBinding
import com.arbonik.icqincrease.navigator
import com.arbonik.icqincrease.presentation.games.OnlineGameViewModel


class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private val viewModel: RegistrationViewModel by viewModels()
    private var gender = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }
    private fun initListeners(){
        binding.buttonRegistration.setOnClickListener {
            val age = binding.age.text.toString()
            if(age.isBlank() || gender.isBlank()){
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.saveData(age.toInt(), gender)
            navigator().showMenuFragment()
        }
        binding.women.setOnClickListener {
            if(gender != WOMEN) {
                gender = WOMEN
                binding.women.backgroundTintList = resources.getColorStateList(R.color.neutral)
                binding.man.backgroundTintList = resources.getColorStateList(R.color.white)
            }
        }
        binding.man.setOnClickListener {
            if(gender != MAN) {
                gender = MAN
                binding.women.backgroundTintList = resources.getColorStateList(R.color.white)
                binding.man.backgroundTintList = resources.getColorStateList(R.color.neutral)
            }
        }
    }

    companion object {
        const val WOMEN = "Women"
        const val MAN = "Man"
        @JvmStatic
        fun newInstance() = RegistrationFragment()
    }
}