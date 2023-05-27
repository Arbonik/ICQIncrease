package com.arbonik.icqincrease.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arbonik.icqincrease.R
import com.arbonik.icqincrease.databinding.FragmentMenuBinding
import com.arbonik.icqincrease.navigator


class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.game1.setOnClickListener {
            navigator().showGameFragment()
        }
        binding.statistic.setOnClickListener {
            navigator().showStatisticsFragment()
        }
        binding.onlineGame.setOnClickListener {
            navigator().showOnlineGameFragment()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }
}