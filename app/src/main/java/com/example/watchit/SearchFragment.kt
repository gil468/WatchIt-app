package com.example.watchit

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.core.view.isNotEmpty
import com.example.watchit.model.MovieApiResponse
import com.squareup.picasso.Picasso
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.runBlocking
import java.net.URLEncoder

class SearchFragment : Fragment() {
    private lateinit var root: View
    private lateinit var searchView: SearchView
    private lateinit var mainLayout: LinearLayout
    private lateinit var pleaseSearchView: TextView
    private lateinit var searchResults: LinearLayout
    private lateinit var httpClient: HttpClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_search, container, false)

        initHttpClient()

        mainLayout = root.findViewById(R.id.linearLayout)
        searchView = root.findViewById(R.id.searchView)
        pleaseSearchView = root.findViewById(R.id.SearchTextView)
        searchResults = root.findViewById(R.id.searchResultsLayout)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchResults.removeAllViews()
                runBlocking { search(query) }
                pleaseSearchView.visibility = View.GONE
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (searchResults.isNotEmpty())
                    pleaseSearchView.visibility = View.GONE
                else
                    pleaseSearchView.visibility = View.VISIBLE

                return false
            }
        })

        return root
    }

    override fun onDestroy() {
        httpClient.close()
        super.onDestroy()
    }

    private fun initHttpClient() {
        httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }
    }

    private suspend fun search(searchText: String) {
        val encodedSearch = URLEncoder.encode(searchText, "UTF-8")

        val apiResponse: MovieApiResponse =
            httpClient.get("https://api.themoviedb.org/3/search/movie?query=$encodedSearch") {
                headers {
                    append(HttpHeaders.Accept, "application/json")
                    append(
                        HttpHeaders.Authorization,
                        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNjhiZTA1NzNhMzY2MTBjMmFhZjMzZDI2NjYxMGMwMSIsInN1YiI6IjY1OWIwM2E4N2Q1NTA0MDI2MTdhMjA4YiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.94z8lgf6EOsxtgyOVyjb_I9AzU_dV5ZUicdRjR6S0EM"
                    )
                }
            }.body()


        apiResponse.results
            .filter { movie -> movie.popularity > 30 }
            .sortedByDescending { movie -> movie.popularity }
            .forEach { movie ->
                val layout = LinearLayout(requireContext())
                val imageView = ImageView(requireContext())

                Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                    .into(imageView)

                val layoutParams = LinearLayout.LayoutParams(200, 200)
                layoutParams.topMargin = 15

                imageView.layoutParams = layoutParams

                val title = TextView(requireContext())
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    weight = 1.0f
                    gravity = Gravity.CENTER
                }
                title.textSize = 20f
                title.text = movie.title
                title.layoutParams = params

                layout.addView(imageView)
                layout.addView(title)

                layout.setOnClickListener {
                    val fragment = MovieFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("movie", movie)
                    fragment.arguments = bundle

                    activity?.supportFragmentManager?.beginTransaction()?.apply {
                        replace(R.id.FragmentLayout, fragment)
                        addToBackStack(null)
                        commit()
                    }
                }

                searchResults.addView(layout)
            }
    }
}