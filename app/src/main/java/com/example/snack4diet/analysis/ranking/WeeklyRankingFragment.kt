package com.example.snack4diet.analysis.ranking

import android.app.Dialog
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
import com.example.snack4diet.databinding.DialogRankingGuideBinding
import com.example.snack4diet.databinding.FragmentWeeklyRankingBinding
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

        binding.search.setOnClickListener {
            validateSearch()
        }

        binding.btnCancel.setOnClickListener {
            setRecyclerView()
        }

        binding.btnRankingGuide.setOnClickListener {
            showRankingGuide()
        }
    }

    private fun setRecyclerView() {
        binding.btnCancel.visibility = View.GONE
        binding.searchData.text.clear()
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

    private fun validateSearch() {
        if (binding.searchData.text.isNullOrEmpty()) return
        else {
            showSearchResult()
        }
    }

    private fun showSearchResult() {
        if (binding.searchData.text.toString() == "중앙대") {
            setSearchRecyclerView()
        }
    }

    private fun setSearchRecyclerView() {
        binding.btnCancel.visibility = View.VISIBLE
        val searchData = binding.searchData.text.toString()
        val searchAdapter = SearchAdapter(emptyList(), searchData)
        binding.rankingRecyclerView.adapter = searchAdapter
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.searchUserLiveData.observe(requireActivity()) { searchResult ->
            searchAdapter.updateData(searchResult)
        }
    }

    private fun showRankingGuide() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ranking_guide)

        val dialogBinding = DialogRankingGuideBinding.bind(dialog.findViewById(R.id.rankingGuideLayout))

        dialogBinding.btnOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }
}