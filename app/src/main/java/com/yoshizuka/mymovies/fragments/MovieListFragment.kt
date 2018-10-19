package com.yoshizuka.mymovies.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.yoshizuka.mymovies.R
import com.yoshizuka.mymovies.adapters.MovieAdapter
import com.yoshizuka.mymovies.models.entities.Movie
import kotlinx.android.synthetic.main.fragment_movie_list.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MovieListFragment : Fragment() {

    /**
     * La liste de données à afficher
     */
    var movies: List<Movie> = arrayListOf()

    /**
     * L'écouteur
     */
    private var mListener: OnMovieListFragmentListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        retainInstance = true
        if(context is OnMovieListFragmentListener) {
            mListener = context as OnMovieListFragmentListener
            mListener?.onCreateMovieListFragment(movies.isEmpty())
        }
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = MovieAdapter(movies)

        movie_list.layoutManager = manager
        movie_list.adapter = adapter
    }

    /**
     * Action du fragement vers l'activity
     */
    interface OnMovieListFragmentListener {

        /**
         * Appelé à la création de la vue
         */
        fun onCreateMovieListFragment(isEmpty: Boolean)
    }

}
