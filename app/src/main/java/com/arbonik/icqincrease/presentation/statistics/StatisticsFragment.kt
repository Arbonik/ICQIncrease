package com.arbonik.icqincrease.presentation.statistics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arbonik.icqincrease.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()
    private val listCharts = mutableListOf("Частота ошибок", "Объём в неделю", "Прогресс")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListView()
    }

    private fun initListView() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            listCharts
        )
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val nameChart = (view as TextView).text.toString()
                val entries = mutableListOf<Entry>()
                entries.add(Entry(1f, 5f))
                entries.add(Entry(2f, 2f))
                entries.add(Entry(3f, 1f))
                entries.add(Entry(4f, -3f))
                entries.add(Entry(5f, 4f))
                entries.add(Entry(6f, 1f))

                val dataset = LineDataSet(entries, nameChart)
                dataset.setDrawFilled(true)
                dataset.color = Color.GREEN
                dataset.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataset.valueTextSize = 20f
                dataset.fillColor = Color.GREEN

                val data = LineData(dataset)
                binding.chart.data = data
                binding.chart.invalidate()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}