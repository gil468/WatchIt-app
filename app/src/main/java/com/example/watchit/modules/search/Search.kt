package com.example.watchit.modules.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.databinding.FragmentSearchBinding

class Search : Fragment() {
    private var searchRecyclerView: RecyclerView? = null
    private var adapter: SearchRecycleAdapter? = null
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SearchViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        searchRecyclerView = binding.searchResultsLayout
        searchRecyclerView?.setHasFixedSize(true)
        searchRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = SearchRecycleAdapter(viewModel.movies.value)

        searchRecyclerView?.adapter = adapter

        viewModel.movies.observe(viewLifecycleOwner) {
            Log.d("TAG", "movies size ${it?.size}")
            adapter?.movies = it
            adapter?.notifyDataSetChanged()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.clearMovies()
                viewModel.refreshMovies(query)

                binding.SearchTextView.visibility = View.GONE
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (viewModel.movies.value?.isNotEmpty() == true)
                    binding.SearchTextView.visibility = View.GONE
                else
                    binding.SearchTextView.visibility = View.VISIBLE

                return false
            }
        })

        return view
    }

}