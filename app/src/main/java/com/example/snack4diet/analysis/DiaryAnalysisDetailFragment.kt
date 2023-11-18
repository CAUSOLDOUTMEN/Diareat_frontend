package com.example.snack4diet.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.api.bestWorst.Best
import com.example.snack4diet.api.bestWorst.Data
import com.example.snack4diet.api.bestWorst.Worst
import com.example.snack4diet.databinding.FragmentDiaryAnalysisDetailBinding

class DiaryAnalysisDetailFragment(private val bestWorst: Data) : Fragment() {
    private lateinit var binding: FragmentDiaryAnalysisDetailBinding
    private lateinit var bestItem: List<Best>
    private lateinit var worstItem: List<Worst>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryAnalysisDetailBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bestItem = bestWorst.best
        worstItem = bestWorst.worst
        binding.totalScore.text = bestWorst.totalScore.toString()
        binding.kcalScore.text = bestWorst.calorieScore.toString()
        binding.carbohydrateScore.text = bestWorst.carbohydrateScore.toString()
        binding.proteinScore.text = bestWorst.proteinScore.toString()
        binding.fatScore.text = bestWorst.fatScore.toString()

        setBestReayclerView()
        setWorstRecyclerView()
    }

    private fun setBestReayclerView() {
        val adapter = BestAdapter(bestItem)
        binding.bestRecyclerView.adapter = adapter
        binding.bestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setWorstRecyclerView() {
        val adapter = WorstAdapter(worstItem)
        binding.worstRecyclerView.adapter = adapter
        binding.worstRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}