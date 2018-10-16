package com.yoshizuka.mymovies.activities

import android.app.SearchManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.SearchView
import android.util.Log
import com.yoshizuka.mymovies.R
import com.yoshizuka.mymovies.adapters.MovieAdapter
import com.yoshizuka.mymovies.fragments.MovieFragment
import com.yoshizuka.mymovies.fragments.MovieListFragment
import com.yoshizuka.mymovies.models.entities.Movie
import com.yoshizuka.mymovies.models.services.MovieApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MovieAdapter.OnMovieAdapterListener {

    /**
     * Création du client api
     */
    private val mClient by lazy {
        MovieApiService.create()
    }

    /**
     *
     */
    private var mDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchViewAction()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        return true
    }

    /**
     * Gestion de la barre de recherche qui va charger le fragment de la liste des films
     */
    private fun searchViewAction() {
        val searchView = toolbar_search
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as? SearchManager?
        searchView.setSearchableInfo(searchManager?.getSearchableInfo(componentName))
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("query", query)
                if(!searchView.isIconified) {
                    searchView.isIconified = true
                }

                mDisposable = mClient.getMovies(query ?: "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result -> showMovies(result.results) },
                        { error -> error.printStackTrace() }
                    )
                // loadFragment
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("query", newText)
                return true
            }
        })
    }

    /**
     * Affiche la liste des données
     * @param movies La liste des données
     */
    private fun showMovies(movies: List<Movie>) {
        val fragment = MovieListFragment()
        fragment.movies = movies
        toolbar_search?.onActionViewCollapsed()


        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        // Si on fait une recharche depuis la vue des détails l'on aura une back stack en trop
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment)


        // Commit the transaction
        transaction.commit()
    }

    private fun showMovieDetail(movie: Movie) {
        setSupportActionBar(toolbar)

        supportActionBar?.title = ""

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)



        val fragment = MovieFragment()
        fragment.movie = movie
        val transaction = supportFragmentManager.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

    /**
     * Lors du click sur un item
     */
    override fun onClick(movie: Movie) {
        Log.d("Movie", "id = ${movie.id}")
        showMovieDetail(movie)
    }
}
