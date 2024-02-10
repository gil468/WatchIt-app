package com.example.watchit.modules.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.data.review.ReviewModel
import com.example.watchit.databinding.FragmentFeedBinding

class Feed : Fragment() {
    private var reviewsRecyclerView: RecyclerView? = null
    private var adapter: FeedRecycleAdapter? = null
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[FeedViewModel::class.java]

        reviewsRecyclerView = binding.reviewsFeed
        reviewsRecyclerView?.setHasFixedSize(true)
        reviewsRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = FeedRecycleAdapter(viewModel.reviews.value, viewModel.users.value)

        reviewsRecyclerView?.adapter = adapter

        viewModel.reviews.observe(viewLifecycleOwner) {
            adapter?.reviews = it
            adapter?.notifyDataSetChanged()
        }

        viewModel.users.observe(viewLifecycleOwner) {
            adapter?.users = it
            adapter?.notifyDataSetChanged()
        }

        binding.pullToRefresh.setOnRefreshListener {
            viewModel.reloadData()
        }

        viewModel.reviewsListLoadingState.observe(viewLifecycleOwner) { state ->
            binding.pullToRefresh.isRefreshing = state == ReviewModel.LoadingState.LOADING
        }

        return view
    }


    override fun onResume() {
        super.onResume()
        viewModel.reloadData()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}