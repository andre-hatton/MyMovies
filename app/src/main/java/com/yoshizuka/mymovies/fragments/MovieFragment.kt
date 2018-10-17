package com.yoshizuka.mymovies.fragments


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

import com.yoshizuka.mymovies.R
import com.yoshizuka.mymovies.managers.Image
import com.yoshizuka.mymovies.models.entities.Movie
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_movie.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MovieFragment : Fragment() {

    /**
     * Le film a initialiser obligatoirement
     */
    lateinit var movie: Movie

    /**
     * L'écouteur
     */
    private var mListener: OnMovieFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        if(context is OnMovieFragmentListener) {
            mListener = context as OnMovieFragmentListener
            mListener?.onCreate(movie)
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movie_name.text = movie.getNameOrTitle()

        Picasso.get().load("${Image.IMAGE_URL}${movie.posterPath}").transform(object :
            Transformation {
            /**
             * Returns a unique key for the transformation, used for caching purposes. If the transformation
             * has parameters (e.g. size, scale factor, etc) then these should be part of the key.
             */
            override fun key(): String {
                return "movie_key"
            }

            /**
             * Transform the source bitmap into a new bitmap. If you create a new bitmap instance, you must
             * call [android.graphics.Bitmap.recycle] on `source`. You may return the original
             * if no transformation is required.
             */
            override fun transform(source: Bitmap): Bitmap {
                // recup de la taille de l'écran
                val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                val min = (if(size.x < size.y) 0.95 * size.x else 0.95 * size.y).toInt()
                val width = source.width
                val height = source.height
                val minBitmap = if(width < height) width else height
                val maxBitmap = if(width > height) width else height
                if(min < minBitmap) {
                    val scale = Bitmap.createScaledBitmap(source, min, min * maxBitmap / minBitmap, true)
                    source.recycle()
                    return scale
                }
                return source
            }

        }).into(movie_image)

        movie_desc.text = movie.overview
    }

    /**
     * Action du fragement vers l'activity
     */
    interface OnMovieFragmentListener {

        /**
         * Appelé à la création de la vue
         */
        fun onCreate(movie: Movie)
    }


}
