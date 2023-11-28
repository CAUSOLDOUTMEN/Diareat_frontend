package com.example.foodfood.analysis.ranking

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.searchUser.SearchUser
import com.example.foodfood.api.searchUserResponse.SearchUserResponse
import com.example.foodfood.api.weeklyRank.Data
import com.example.foodfood.databinding.DialogRankingGuideBinding
import com.example.foodfood.databinding.FragmentWeeklyRankingBinding
import kotlinx.coroutines.launch

class WeeklyRankingFragment(private var rankList: List<Data>) : Fragment() {
    private lateinit var binding: FragmentWeeklyRankingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainActivity: MainActivity
    private var searchResponse: SearchUserResponse? = null
    private var userId = -1L

    interface FollowListener{
        fun followUser(followId: Long)
        fun unfollowUser(followId: Long)
    }

    private val followListener = object: FollowListener{
        override fun followUser(followId: Long) {
            mainActivity.followUser(followId)
        }

        override fun unfollowUser(followId: Long) {
            mainActivity.unfollowUser(followId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("id", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyRankingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecyclerView()

        binding.search.setOnClickListener {
            validateSearch()
        }

        binding.searchData.setOnKeyListener { v, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KEYCODE_ENTER) {
                validateSearch()
                hideKeyboard(v)
            }
            true
        }

        binding.btnCancel.setOnClickListener {
            setRecyclerView()
        }

        binding.btnRankingGuide.setOnClickListener {
            showRankingGuide()
        }
    }

    private fun setRecyclerView() {
        binding.noSearchResults.visibility = View.GONE
        binding.recyclerFrame.visibility = View.VISIBLE
        lifecycleScope.launch {
            rankList = mainActivity.getWeeklyRankingData()!!
            binding.btnCancel.visibility = View.GONE
            binding.searchData.text.clear()
            val adapter = RankingAdapter(requireContext(), rankList, userId)
            binding.rankingRecyclerView.adapter = adapter
            binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter.showBottomSheet { position ->
                rankingBottomSheet(position)
            }
        }
    }

    private fun rankingBottomSheet(position: Int) {
        val bottomSheetFragment = RankingBottomSheetFragment(rankList[position])
        val bundle = Bundle()
        bundle.putInt("position", position) // 아이템 위치 전달
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCustomTheme)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun validateSearch() {
        if (binding.searchData.text.isNullOrEmpty()) return
        else {
            showSearchResult(binding.searchData.text.toString())
        }
    }

    private fun showSearchResult(inputName: String) {
        val user = SearchUser(inputName, userId)
        lifecycleScope.launch {
            searchResponse = mainActivity.searchUser(user)
            if (searchResponse?.data.isNullOrEmpty()) {
                binding.noSearchResults.visibility = View.VISIBLE
                binding.recyclerFrame.visibility = View.GONE
                binding.btnCancel.visibility = View.VISIBLE
            } else {
                binding.noSearchResults.visibility = View.GONE
                binding.recyclerFrame.visibility = View.VISIBLE
                setSearchRecyclerView()
            }
        }
    }

    private fun setSearchRecyclerView() {
        binding.noSearchResults.visibility = View.GONE
        binding.btnCancel.visibility = View.VISIBLE
        val searchData = binding.searchData.text.toString()
        val searchAdapter = SearchAdapter(requireContext(), searchResponse!!.data, searchData, followListener)
        binding.rankingRecyclerView.adapter = searchAdapter
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}