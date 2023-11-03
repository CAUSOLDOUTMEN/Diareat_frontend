package com.example.snack4diet.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.databinding.FragmentDiaryAnalysisDetailBinding

class DiaryAnalysisDetailFragment : Fragment() {
    private lateinit var binding: FragmentDiaryAnalysisDetailBinding
    private lateinit var bestItem: List<Macronutrients>
    private lateinit var worstItem: List<Macronutrients>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryAnalysisDetailBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bestItem = listOf(
            Macronutrients(1,"음식1",500, 32,11,21, false),
            Macronutrients(1,"음식1",500, 32,11,21, false),
            Macronutrients(1,"음식1",500, 32,11,21, false)
        )

        worstItem = listOf(
            Macronutrients(1,"음식1",500, 32,11,21, false),
            Macronutrients(1,"음식1",500, 32,11,21, false),
            Macronutrients(1,"음식1",500, 32,11,21, false)
        )

        setBestReayclerView()
        setWorstRecyclerView()
    }

    private fun setBestReayclerView() {
        val adapter = BestWorstAdapter(bestItem)
        binding.bestRecyclerView.adapter = adapter
        binding.bestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setWorstRecyclerView() {
        val adapter = BestWorstAdapter(worstItem)
        binding.worstRecyclerView.adapter = adapter
        binding.worstRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}