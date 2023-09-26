package com.example.snack4diet.bookmark

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.databinding.FragmentBookmarkBinding
import com.example.snack4diet.viewModel.NutrientsViewModel

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var bookmarkList: MutableList<Macronutrients>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //뷰모델 초기화
        viewModel = (requireActivity() as MainActivity).getViewModel()
        bookmarkList = mutableListOf()

        //리사이클러뷰 어댑터 설정
        bookmarkAdapter = BookmarkAdapter(emptyList()) { nutrient ->
            viewModel.deleteBookmark(nutrient)
            bookmarkAdapter.notifyDataSetChanged()
        }

        binding.recyclerView.adapter = bookmarkAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.nutrientsLiveData.observe(requireActivity()) { nutrients ->

            for (nutrient in nutrients) {
                if (nutrient.isBookmark) {
                    bookmarkList.add(nutrient)
                }
            }
            bookmarkAdapter.nutrients = bookmarkList
            bookmarkAdapter.notifyDataSetChanged()
        }
    }
}