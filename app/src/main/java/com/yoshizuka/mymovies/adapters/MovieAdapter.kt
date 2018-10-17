package com.yoshizuka.mymovies.adapters

import android.content.Context
import android.graphics.Point
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.squareup.picasso.Picasso
import com.yoshizuka.mymovies.R
import com.yoshizuka.mymovies.managers.Image
import com.yoshizuka.mymovies.models.entities.Movie
import kotlinx.android.synthetic.main.adapter_movie.view.*
import kotlin.math.roundToInt

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
    private var mListener: OnMovieAdapterListener? = null

    /**
     * Le context intialiser dans le onCreateViewHolder
     */
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        mContext = parent.context;
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
        holder.name.text = movie.getNameOrTitle()

        // recup de la taille de l'écran
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val min = if(size.x < size.y) size.x else size.y
        val imgWidth = min * 0.23f

        Picasso.get().load("${Image.IMAGE_URL}${movie.posterPath}").resize(imgWidth.toInt(), 0).into(holder.image)

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