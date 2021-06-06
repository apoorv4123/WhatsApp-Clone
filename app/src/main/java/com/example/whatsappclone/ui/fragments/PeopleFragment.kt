package com.example.whatsappclone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.modals.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_chats.*
import java.lang.Exception

class PeopleFragment : Fragment() { // We'll show the list of users inside people fragment

    lateinit var mAdapter: FirestorePagingAdapter<User, UsersViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name", Query.Direction.ASCENDING)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupAdapter()
        return inflater.inflate(
            R.layout.fragment_chats,
            container,
            false
        ) // Inflate the layout for this fragment
    }

    private fun setupAdapter() {
        val config = PagedList.Config.Builder()
            .setPrefetchDistance(2)
            .setPageSize(10)
            .setEnablePlaceholders(false)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(viewLifecycleOwner)
            .setQuery(database, config, User::class.java)
            .build()

        mAdapter = object : FirestorePagingAdapter<User, UsersViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
                val view = layoutInflater.inflate(R.layout.list_item, parent, false)
                return UsersViewHolder(view)
            }

            override fun onBindViewHolder(holder: UsersViewHolder, position: Int, model: User) {
                holder.bind(user = model)
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                    }
                    LoadingState.LOADING_MORE -> {
                    }
                    LoadingState.LOADED -> {
                    }
                    LoadingState.FINISHED -> {
                    }
                    LoadingState.ERROR -> {
                    }
                }
            }

            override fun onError(e: Exception) {
                super.onError(e)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

}
/**
 * You have 1000 users
 * 1 user - 10kb
 * 10 * 1000 = 10000kb - You may get a timeout
 * Pagination - Getting data in pages
 */