package com.yoshizuka.mymovies.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.yoshizuka.mymovies.R
import com.yoshizuka.mymovies.models.entities.Movie
import kotlinx.android.synthetic.main.adapter_movie.view.*

/**
 * Gestion des items de la liste
 */
class MovieAdapter(
    /**
     * Liste des données à afficher
     */
    private var movies: List<Movie> = arrayListOf()
): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    /**
     * L'écouteur de l'adapter
     */
    var mListener: OnMovieAdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        if(parent.context is OnMovieAdapterListener) {
            mListener = parent.context as OnMovieAdapterListener
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size


    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        Log.d("Adapter", "Titre : " + if(movie.name == "") movie.title else movie.name)
        if(movie.title == "")
            holder.name.text = movie.name
        else
            holder.name.text = movie.title

        Picasso.get().load("https://image.tmdb.org/t/p/h100/" + movie.posterPath).into(holder.image)

        holder.itemView.setOnClickListener { mListener?.onClick(movie) }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         * L'image de l'item
         */
        val image = itemView.movie_image_mini

        /**
         * Nom de l'item
         */
        val name = itemView.movie_name

    }

    /**
     * Ecouteur de l'adapter
     */
    interface OnMovieAdapterListener {

        /**
         * Lors du click sur un item
         */
        fun onClick(movie: Movie)
    }
}