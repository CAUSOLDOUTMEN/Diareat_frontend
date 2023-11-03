package com.example.snack4diet.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.snack4diet.R
import com.example.snack4diet.analysis.ranking.WeeklyRankingFragment
import com.example.snack4diet.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {
    private lateinit var binding: FragmentAnalysisBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDiaryAnalysis.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.underLine2.visibility = View.GONE

        setDiaryAnalysisFragment()

        binding.btnDiaryAnalysis.setOnClickListener {
            setDiaryAnalysisFragment()
        }

        binding.btnWeeklyRanking.setOnClickListener {
            setWeeklyRankingFragment()
        }
    }

    private fun setDiaryAnalysisFragment() {
        val diaryAnalysisFragment = DiaryAnalysisFragment()

        binding.btnDiaryAnalysis.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.btnWeeklyRanking.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.underLine1.visibility = View.VISIBLE
        binding.underLine2.visibility = View.GONE

        replaceChildFragment(diaryAnalysisFragment, "diaryAnalysisFragment")
    }

    private fun setWeeklyRankingFragment() {
        val weeklyRankingFragment = WeeklyRankingFragment()

        binding.btnDiaryAnalysis.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.btnWeeklyRanking.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.underLine2.visibility = View.VISIBLE
        binding.underLine1.visibility = View.GONE

        replaceChildFragment(weeklyRankingFragment, "weeklyRankingFragment")
    }

    private fun replaceChildFragment(fragment: Fragment, tag: String) {
        if (!isAdded) return
        childFragmentManager.beginTransaction()
            .replace(R.id.analysisSubFrame, fragment, tag)
            .commit()
    }
}