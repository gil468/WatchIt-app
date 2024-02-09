package com.example.watchit.modules.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.data.Model
import com.example.watchit.databinding.FragmentFeedBinding

class Feed : Fragment() {
    var reviewsRecyclerView: RecyclerView? = null
    var adapter: ReviewsRecycleAdapter? = null

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]

        viewModel.reviews = Model.instance.getAllReviews()
        viewModel.users = Model.instance.getAllUsers()

        reviewsRecyclerView = binding.reviewsFeed
        reviewsRecyclerView?.setHasFixedSize(true)
        reviewsRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = ReviewsRecycleAdapter(viewModel.reviews?.value, viewModel.users?.value)

        reviewsRecyclerView?.adapter = adapter


        viewModel.reviews?.observe(viewLifecycleOwner) {
            adapter?.reviews = it
            adapter?.notifyDataSetChanged()
        }

        viewModel.users?.observe(viewLifecycleOwner) {
            adapter?.users = it
            adapter?.notifyDataSetChanged()
        }

        binding.pullToRefresh.setOnRefreshListener {
            reloadData()
        }

        Model.instance.reviewsListLoadingState.observe(viewLifecycleOwner) { state ->
            binding.pullToRefresh.isRefreshing = state == Model.LoadingState.LOADING
        }

        return view
    }


    override fun onResume() {
        super.onResume()
        reloadData()
    }

    private fun reloadData() {
        Model.instance.refreshAllUsers()
        Model.instance.refreshAllReviews()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}