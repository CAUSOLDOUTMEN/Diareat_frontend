package com.example.snack4diet.analysis.ranking

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.UserRank
import com.example.snack4diet.databinding.FragmentWeeklyRankingBinding
import com.example.snack4diet.home.BottomSheetFragment
import com.example.snack4diet.viewModel.NutrientsViewModel

class WeeklyRankingFragment : Fragment() {
    private lateinit var binding: FragmentWeeklyRankingBinding
    private lateinit var followers: List<UserRank>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: NutrientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyRankingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        val mainActivity = requireActivity() as MainActivity
        viewModel = mainActivity.getViewModel()

        setRecyclerView()
    }

    private fun setRecyclerView() {
        val adapter = RankingAdapter(emptyList(), 4)
        binding.rankingRecyclerView.adapter = adapter
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.showBottomSheet { position ->
            rankingBottomSheet(position)
        }
        viewModel.followingLiveData.observe(requireActivity()) { followingLiveData ->
            adapter.updateData(followingLiveData)
        }
    }

    private fun rankingBottomSheet(position: Int) {
        val bottomSheetFragment = RankingBottomSheetFragment()
        val bundle = Bundle()
        bundle.putInt("position", position) // 아이템 위치 전달
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCustomTheme)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}