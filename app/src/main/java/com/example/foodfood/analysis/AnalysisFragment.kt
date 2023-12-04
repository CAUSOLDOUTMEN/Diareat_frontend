package com.example.foodfood.analysis

import android.content.SharedPreferences
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
import com.example.foodfood.loading.DialogLoading
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
    private lateinit var progressDialog: DialogLoading
    private lateinit var sharedPreferences: SharedPreferences
    private var accessToken = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(layoutInflater, container, false)

        mainActivity = requireActivity() as MainActivity
        app = mainActivity.application
        sharedPreferences = app.getSharedPrefs()
        accessToken = sharedPreferences.getString("accessToken", "")!!
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
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth

                val response = app.apiService.getAnalysisGraphData(accessToken, userId, day, month, year)
                Log.e("뭐가 문젠데ㅔㅔㅔㅔㅔㅔㅔㅔㅔ", response.toString())
                graphData = response.data
                Log.e("보여줘라", response.data.toString())
            } catch (e: Exception) {
                Log.e("AnalysisFragment", "Error during getAnalysisGraphData API call", e)
            } finally {
                progressDialog.dismiss()
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
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth
                val response = app.apiService.getWeeklyRank(accessToken, userId, day, month, year)
                rankList = response.data
                Log.e("너느 뭐임???", rankList.toString())
            } catch (e: Exception) {
                Log.e("AnalysisFragment", "Error during getWeeklyRank API call", e)
            } finally {
                progressDialog.dismiss()
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

    private fun showProgressDialog() {
        progressDialog = DialogLoading(requireContext())
        progressDialog.show()
    }
}