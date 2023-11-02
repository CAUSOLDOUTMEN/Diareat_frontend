package com.example.snack4diet.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.snack4diet.R
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
    }

    private fun setDiaryAnalysisFragment() {
        val diaryAnalysisFragment = DiaryAnalysisFragment()

        replaceChildFragment(diaryAnalysisFragment, "diaryAnalysisFragment")
    }

    private fun replaceChildFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .replace(R.id.subFrame, fragment, tag)
            .addToBackStack(null) // 이전 프래그먼트를 백스택에 추가
            .commit()
    }
}