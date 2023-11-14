package com.example.snack4diet.bookmark

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.getBookmark.Data
import com.example.snack4diet.databinding.AddDiaryDialogLayoutBinding
import com.example.snack4diet.databinding.FragmentBookmarkBinding
import com.example.snack4diet.viewModel.NutrientsViewModel

class BookmarkFragment : Fragment() {
    private lateinit var binding: FragmentBookmarkBinding
//    private lateinit var viewModel: NutrientsViewModel
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var bookmarkList: List<Data>

    interface OnItemClickListener {
        fun onItemClick(id: Int)
    }

    private val itemClickListener = object : OnItemClickListener {
        override fun onItemClick(id: Int) {
            showAddDiaryDialog(id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        bookmarkList = mainActivity.getBookmarkList()
        Log.e("뭔데뭔데뭔데뭔데뭔데", bookmarkList.toString())
    }

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
//        viewModel = (requireActivity() as MainActivity).getViewModel()

        //리사이클러뷰 어댑터 설정
//        bookmarkAdapter = BookmarkAdapter(bookmarkList, itemClickListener) { nutrient ->
//            viewModel.deleteBookmark(nutrient)
//            setViewModel()
//            bookmarkAdapter.notifyDataSetChanged()
//        }

        bookmarkAdapter = BookmarkAdapter(bookmarkList)

        binding.recyclerView.adapter = bookmarkAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        setViewModel()
    }

//    private fun setViewModel() {
//        viewModel.bookmarkLiveData.observe(requireActivity()) { bookmarkList ->
//            bookmarkAdapter.nutrients = bookmarkList
//            bookmarkAdapter.notifyDataSetChanged()
//        }
//    }

    private fun showAddDiaryDialog(id: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_diary_dialog_layout)

        val dialogBinding = AddDiaryDialogLayoutBinding.bind(dialog.findViewById(R.id.dialogLayout))

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
//            addDiary(id)
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }

    private fun addDiary(id: Int) {
//        viewModel.bookmarkLiveData.observe(requireActivity()) { bookmarkList ->
//            viewModel.registerDiary(bookmarkList.find { it.favoriteFoodId == id }!!)
//
//            Toast.makeText(requireContext(), "등록되었습니다",
//            Toast.LENGTH_SHORT).show()
//        }
    }
}