package com.example.snack4diet.bookmark

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.getBookmark.Data
import com.example.snack4diet.api.updateFavoriteFood.BaseNutrition
import com.example.snack4diet.api.updateFavoriteFood.UpdateFavoriteFoodDto
import com.example.snack4diet.databinding.AddDiaryDialogLayoutBinding
import com.example.snack4diet.databinding.DialogDeleteBookmarkBinding
import com.example.snack4diet.databinding.FragmentBookmarkBinding
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment(), FragmentResultListener {
    private lateinit var binding: FragmentBookmarkBinding
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var bookmarkList: List<Data>
    private var userId = -1L

    interface OnItemClickListener {
        fun addToDiary(id: Long)
        fun deleteBookmark(favoriteFoodId: Long)
        fun updateBookmark(favoriteFoodId: Long)
    }

    private val onItemClickListener = object : OnItemClickListener {
        override fun addToDiary(id: Long) {
            showAddDiaryDialog(id)
        }

        override fun deleteBookmark(favoriteFoodId: Long) {
            showDeleteBookmarkDialog(favoriteFoodId)
        }

        override fun updateBookmark(favoriteFoodId: Long) {
            showBookmarkEditBottomSheet(favoriteFoodId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        userId = mainActivity.getUserId()

        parentFragmentManager.setFragmentResultListener("bookmarkEditBottomSheetResult", this, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch {
            bookmarkList = mainActivity.getBookmarkList()!!
            bookmarkAdapter = BookmarkAdapter(bookmarkList, onItemClickListener)
            binding.recyclerView.adapter = bookmarkAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            Log.e("뭐냐고뭐냐고뭐냐고뭐냐고", bookmarkList.toString())
        }

        return binding.root
    }

    private fun showAddDiaryDialog(id: Long) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.add_diary_dialog_layout)

        val dialogBinding = AddDiaryDialogLayoutBinding.bind(dialog.findViewById(R.id.dialogLayout))

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            addDiary(id)
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }

    private fun showDeleteBookmarkDialog(id: Long) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_bookmark)

        val dialogBinding = DialogDeleteBookmarkBinding.bind(dialog.findViewById(R.id.deleteBookmarkDialogLayout))

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                mainActivity.deleteBookmark(id)

                bookmarkList = mainActivity.getBookmarkList()!!
                bookmarkAdapter.notifyDataSetChanged()
            }
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }

    private fun showBookmarkEditBottomSheet(id: Long) {
        val bookmarkItem = bookmarkList.find { it.favoriteFoodId == id }!!
        Log.e("뭔데뭔데", bookmarkItem.toString())
        val baseNutrition = BaseNutrition(bookmarkItem.baseNutrition.carbohydrate, bookmarkItem.baseNutrition.fat, bookmarkItem.baseNutrition.kcal, bookmarkItem.baseNutrition.protein)
        val favoriteFoodId = bookmarkItem.favoriteFoodId
        val name = bookmarkItem.name
        val updateFavoriteFoodDto = UpdateFavoriteFoodDto(baseNutrition, favoriteFoodId, name, userId)

        val bottomSheetFragment = BookmarkEditBottomSheetFragment(updateFavoriteFoodDto)

        bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCustomTheme)
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    private fun addDiary(id: Long) {

    }

    fun getBookmarkList() {
        lifecycleScope.launch {
            bookmarkList = mainActivity.getBookmarkList()!!
            bookmarkAdapter.notifyDataSetChanged()
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == "bottomSheetResult") {
            // 작업 완료 시 호출되는 로직
            if (result.getBoolean("actionCompleted", false)) {
                getBookmarkList()
            }
        }
    }// FragmentResultListener를 사용하기 위해 구현해야 하는 함수
}