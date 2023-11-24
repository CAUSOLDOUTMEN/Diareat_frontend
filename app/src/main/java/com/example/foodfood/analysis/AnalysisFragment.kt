package com.example.foodfood.analysis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.analysis.ranking.WeeklyRankingFragment
import com.example.foodfood.api.analysisGraph.Data
import com.example.foodfood.application.MyApplication
import com.example.foodfood.databinding.FragmentAnalysisBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AnalysisFragment : Fragment() {
    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var app: MyApplication
    private lateinit var mainActivity: MainActivity
    private lateinit var graphData: Data
    private lateinit var rankList: List<com.example.foodfood.api.weeklyRank.Data>
    private var userId = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(layoutInflater, container, false)

        mainActivity = requireActivity() as MainActivity
        app = mainActivity.application
        userId = mainActivity.getUserId()

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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth

                val response = app.apiService.getAnalysisGraphData(userId, day, month, year)
                graphData = response.data
                Log.e("보여줘라", response.data.toString())
            } catch (e: Exception) {
                Log.e("AnalysisFragment", "Error during getAnalysisGraphData API call", e)
            }

            withContext(Dispatchers.Main) {
                val diaryAnalysisFragment = DiaryAnalysisFragment(graphData)

                binding.btnDiaryAnalysis.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.btnWeeklyRanking.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                binding.underLine1.visibility = View.VISIBLE
                binding.underLine2.visibility = View.GONE

                replaceChildFragment(diaryAnalysisFragment, "diaryAnalysisFragment")
            }
        }
    }

    private fun setWeeklyRankingFragment() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth
                val response = app.apiService.getWeeklyRank(userId, day, month, year)
                rankList = response.data
                Log.e("너느 뭐임???", rankList.toString())
            } catch (e: Exception) {
                Log.e("AnalysisFragment", "Error during getWeeklyRank API call", e)
            }

            withContext(Dispatchers.Main) {
                val weeklyRankingFragment = WeeklyRankingFragment(rankList)

                binding.btnDiaryAnalysis.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                binding.btnWeeklyRanking.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                binding.underLine2.visibility = View.VISIBLE
                binding.underLine1.visibility = View.GONE

                replaceChildFragment(weeklyRankingFragment, "weeklyRankingFragment")
            }
        }
    }

    private fun replaceChildFragment(fragment: Fragment, tag: String) {
        if (!isAdded) return
        childFragmentManager.beginTransaction()
            .replace(R.id.analysisSubFrame, fragment, tag)
            .commit()
    }
}