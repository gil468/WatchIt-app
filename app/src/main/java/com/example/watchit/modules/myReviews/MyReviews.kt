package com.example.watchit.modules.myReviews

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

class MyReviews : Fragment() {
    private var reviewsRecyclerView: RecyclerView? = null
    private var adapter: MyReviewsRecycleAdapter? = null
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MyReviewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[MyReviewsViewModel::class.java]

        viewModel.reviews = Model.instance.getMyReviews()
        viewModel.user = Model.instance.getCurrentUser()

        reviewsRecyclerView = binding.reviewsFeed
        reviewsRecyclerView?.setHasFixedSize(true)
        reviewsRecyclerView?.layoutManager = LinearLayoutManager(context)
        adapter = MyReviewsRecycleAdapter(viewModel.reviews?.value, viewModel.user?.value)

        reviewsRecyclerView?.adapter = adapter


        viewModel.reviews?.observe(viewLifecycleOwner) {
            adapter?.reviews = it
            adapter?.notifyDataSetChanged()
        }

        viewModel.user?.observe(viewLifecycleOwner) {
            adapter?.user = it
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