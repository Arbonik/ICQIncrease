package com.arbonik.icqincrease

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.arbonik.icqincrease.databinding.ActivityMainBinding
import com.arbonik.icqincrease.presentation.MenuFragment
import com.arbonik.icqincrease.presentation.games.GameFragment
import com.arbonik.icqincrease.presentation.statistics.StatisticsFragment

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun showGameFragment() = launch(GameFragment.newInstance())
    override fun showStatisticsFragment() = launch(StatisticsFragment.newInstance())
    override fun showMenuFragment() = launch(MenuFragment.newInstance())

    private fun launch(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}