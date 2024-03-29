package com.demo.androidfundamentals

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.androidfundamentals.adapter.MoviesAdapter
import com.demo.androidfundamentals.databinding.ActivityMainBinding
import com.demo.androidfundamentals.databinding.FilterBottomSheetBinding
import com.demo.androidfundamentals.databinding.SortBottomSheetBinding
import com.demo.androidfundamentals.models.MovieModel
import com.demo.androidfundamentals.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val moviesAdapter = MoviesAdapter()
    lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var sortBottomSheetBinding: SortBottomSheetBinding
    private lateinit var sortDialog: BottomSheetDialog
    private lateinit var filterBottomSheetBinding: FilterBottomSheetBinding
    private lateinit var filterDialog: BottomSheetDialog
    private val moviesList = mutableListOf<MovieModel>()

    enum class SortType {
        Asc, Desc
    }

    enum class SortBy {
        Name, ReleaseDate
    }

    private var selectedSortType = SortType.Asc
    private var selectedSortBy = SortBy.Name


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gridLayoutManager = GridLayoutManager(this@MainActivity, 2)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.recyclerView.adapter = moviesAdapter
        binding.recyclerView.layoutManager = gridLayoutManager

        viewModel.apiStatus.observe(this) {
            when (it) {

                is MainViewModel.ApiStatus.Success -> {
                    moviesList.addAll(it.apiModel.results)
                    loadFilteredAndSortedMovies(selectedSortBy, selectedSortType)
                    binding.progressBar.visibility = View.GONE
                    initSortBottomSheet()
                    initFilterBottomSheet()
                }
                is MainViewModel.ApiStatus.Loader -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                else -> {}
            }
        }
        viewModel.fetchMovieList()

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.fetchMovieList(true)
                }
            }
        })

        binding.sortBtn.setOnClickListener {
            sortDialog.show()
        }

        binding.filterBtn.setOnClickListener {
            filterDialog.show()
        }
    }

    private fun initFilterBottomSheet() {
        filterDialog = BottomSheetDialog(this)
        filterBottomSheetBinding = FilterBottomSheetBinding.inflate(layoutInflater)
        filterDialog.setContentView(filterBottomSheetBinding.root)
        val chipGroup = filterBottomSheetBinding.chipGroup

        moviesList.getDistinctMovieYears().forEach {
            chipGroup.addView(createTagChip(this, it))
        }

        filterBottomSheetBinding.applyButton.setOnClickListener {
            val filterList = chipGroup.checkedChipIds.map { id ->
                chipGroup.findViewById<Chip>(id).text.toString()
            }
            loadFilteredAndSortedMovies(selectedSortBy, selectedSortType, filterList)
            filterDialog.dismiss()
            Log.d("filterList", filterList.toString())
        }
    }

    private fun initSortBottomSheet() {
        sortDialog = BottomSheetDialog(this)
        sortBottomSheetBinding = SortBottomSheetBinding.inflate(layoutInflater)
        sortBottomSheetBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSortBy =
                if (checkedId == sortBottomSheetBinding.byName.id) SortBy.Name else SortBy.ReleaseDate
            loadSortByData(selectedSortBy)
            sortDialog.dismiss()
        }

        sortBottomSheetBinding.toggleButton.setOnClickListener {
            selectedSortType = if (selectedSortType == SortType.Asc) SortType.Desc else SortType.Asc
            loadSortTypeData(selectedSortType)
            sortDialog.dismiss()
        }

        // load default data
        if (selectedSortBy == SortBy.Name) {
            sortBottomSheetBinding.byName.isChecked = true
        } else {
            sortBottomSheetBinding.byReleaseDate.isChecked = true
        }
        loadSortTypeData(selectedSortType)
        //

        sortDialog.setContentView(sortBottomSheetBinding.root)
    }

    private fun loadFilteredAndSortedMovies(sortBy: SortBy, sortType: SortType, filteredList: List<String> = mutableListOf()) {
        val filteredAndSortedMovies = moviesList.applyFilterAndSort(sortBy, sortType, filteredList)
        moviesAdapter.populateData(filteredAndSortedMovies)
    }

    private fun loadSortByData(sortBy: SortBy) {
        if (sortBy == SortBy.Name) {
            Toast.makeText(this, "By name clicked", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "By release date clicked", Toast.LENGTH_SHORT).show()
        }
        loadFilteredAndSortedMovies(selectedSortBy, selectedSortType)
    }

    private fun loadSortTypeData(sortType: SortType) {
        if (sortType == SortType.Asc) {
            sortBottomSheetBinding.toggleButton.setImageResource(R.drawable.ic_baseline_arrow_upward_24)
        } else {
            sortBottomSheetBinding.toggleButton.setImageResource(R.drawable.ic_baseline_arrow_downward_24)
        }
        loadFilteredAndSortedMovies(selectedSortBy, selectedSortType)
    }

    private fun createTagChip(context: Context, chipName: String): Chip {
        return Chip(context).apply {
            text = chipName
            isCheckable = true
        }
    }
}