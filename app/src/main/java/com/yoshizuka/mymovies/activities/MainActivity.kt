package com.yoshizuka.mymovies.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.yoshizuka.mymovies.R
import com.yoshizuka.mymovies.adapters.MovieAdapter
import com.yoshizuka.mymovies.fragments.MovieFragment
import com.yoshizuka.mymovies.fragments.MovieListFragment
import com.yoshizuka.mymovies.managers.Image
import com.yoshizuka.mymovies.models.entities.Movie
import com.yoshizuka.mymovies.models.services.MovieApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MovieAdapter.OnMovieAdapterListener, MovieFragment.OnMovieFragmentListener,
    MovieListFragment.OnMovieListFragmentListener {

    /**
     * Création du client api
     */
    private val mClient by lazy {
        MovieApiService.create()
    }

    /**
     * Dernier film selectionné dans la liste
     */
    private var mCurrentMovie : Movie? = null

    /**
     * Reference à l'observeur permettant de la stopper avant de recevoir le resultat
     */
    private var mDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchViewAction()
    }

    /**
     * Appelé lors de l'action retour depuis le détail d'un film
     * @return true si l'action a réussi
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Gestion de l'action retour
     */
    override fun onBackPressed() {
        super.onBackPressed()

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)


        toolbar_search.visibility = View.VISIBLE
        toolbar_share.visibility = View.GONE
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

        // Vide le texte du quand le container est vide
        if(movies.isEmpty()) {
            container_empty.text = getString(R.string.empty_movie)
        } else {
            container_empty.text = ""
        }


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

    /**
     * Charge le détail d'un film
     * @param movie Le film à charger
     */
    private fun showMovieDetail(movie: Movie) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)

        val fragment = MovieFragment()
        fragment.movie = movie
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)

        // Commit the transaction
        transaction.commit()
    }

    /**
     * Action du bouton de partage
     * @param view La vue de l'event
     */
    fun shared(view: View) {
        if(mCurrentMovie != null) {
            Image.shareImage(this, Image.IMAGE_URL, mCurrentMovie!!)
        } else {
            Toast.makeText(this, "Partage de ${mCurrentMovie?.getNameOrTitle()} échoué", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Lors du click sur un item
     * @param movie Le film choisi
     */
    override fun onClick(movie: Movie) {
        mCurrentMovie = movie
        showMovieDetail(movie)
    }

    /**
     * Appelé sur la création du fragment de la liste des films
     * @param isEmpty Si la liste est vide
     */
    override fun onCreateMovieListFragment(isEmpty: Boolean) {
        // Vide le texte du quand le container est vide
        if(isEmpty) {
            container_empty.text = getString(R.string.empty_movie)
        } else {
            container_empty.text = ""
        }
    }

    /**
     * Appelé à la création de la vue des détails d'un film
     * @param movie Le film de la fiche
     */
    override fun onCreateMovieFragment(movie: Movie) {
        mCurrentMovie = movie
        setSupportActionBar(toolbar)

        container_empty.text = ""

        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar_search.visibility = View.GONE
        toolbar_share.visibility = View.VISIBLE
    }
}
