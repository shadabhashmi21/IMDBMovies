package com.demo.androidfundamentals.adapter

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.demo.androidfundamentals.MainActivity
import com.demo.androidfundamentals.databinding.CardLayoutBinding
import com.demo.androidfundamentals.models.MovieModel
import com.squareup.picasso.Picasso
class MoviesAdapter : RecyclerView.Adapter<MoviesAdapter.ViewHolder>() {

    private val movieList: MutableList<MovieModel> = mutableListOf()

    fun populateData(results: List<MovieModel>) {
        movieList.clear()
        movieList.addAll(results)
        notifyDataSetChanged()
    }

    inner class ViewHolder(binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val moviePoster: ImageView = binding.moviePoster
        val movieTitle: TextView = binding.movieTitle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = movieList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movieList[position]
        val imgUrl = "https://image.tmdb.org/t/p/w500/${movie.posterUrl}"
        d("Example", "Name : ${movie.title}")
        holder.movieTitle.text = movie.title
        Picasso.get().load(imgUrl).into(holder.moviePoster)
    }
}

