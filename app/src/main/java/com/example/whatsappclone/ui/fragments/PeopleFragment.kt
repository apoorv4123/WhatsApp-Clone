package com.example.whatsappclone.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappclone.R
import com.example.whatsappclone.modals.User
import com.example.whatsappclone.ui.ChatActivity
import com.example.whatsappclone.ui.IMAGE
import com.example.whatsappclone.ui.NAME
import com.example.whatsappclone.ui.UID
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_chats.*
import java.lang.Exception

private const val DELETED_VIEW_TYPE = 1
private const val NORMAL_VIEW_TYPE = 2

class PeopleFragment : Fragment() { // We'll show the list of users inside people fragment

    lateinit var mAdapter: FirestorePagingAdapter<User, RecyclerView.ViewHolder>
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

        mAdapter = object : FirestorePagingAdapter<User, RecyclerView.ViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return when (viewType) {
                    NORMAL_VIEW_TYPE -> UsersViewHolder(layoutInflater.inflate(R.layout.list_item, parent, false))
                    else -> EmptyViewHolder(
                        layoutInflater.inflate(R.layout.empty_view, parent, false))
                }
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, model: User) {
                if (holder is UsersViewHolder) {
                    holder.bind(user = model) { name: String, photo: String, id: String ->
                        // call startActivity with all the intent arguments
                        val intent = Intent(requireContext(), ChatActivity::class.java)
                        intent.putExtra(UID,id)
                        intent.putExtra(NAME , name)
                        intent.putExtra(IMAGE, photo)
                        startActivity(intent)
                    }
                } else {
                    // Todo - Something
                }
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

            override fun getItemViewType(position: Int): Int {
                val item =
                    getItem(position)?.toObject(User::class.java) // Get item at current position & convert it into user class
                return if (auth.uid == item!!.uid) { // This can be used to handle blocklist too
                    DELETED_VIEW_TYPE
                } else {
                    NORMAL_VIEW_TYPE
                }
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