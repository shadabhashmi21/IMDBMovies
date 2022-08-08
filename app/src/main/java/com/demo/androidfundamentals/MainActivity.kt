package com.demo.androidfundamentals

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.androidfundamentals.adapter.MoviesAdapter
import com.demo.androidfundamentals.adapter.SortType
import com.demo.androidfundamentals.databinding.ActivityMainBinding
import com.demo.androidfundamentals.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val moviesAdapter = MoviesAdapter()
    lateinit var gridLayoutManager: GridLayoutManager


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
                    moviesAdapter.populateData(it.apiModel.results.toMutableList())
                    binding.progressBar.visibility = View.GONE
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
            moviesAdapter.sort()
        }
    }
}