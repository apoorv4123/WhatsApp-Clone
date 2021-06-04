package com.example.whatsappclone.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.modals.User
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_chats.*

class PeopleFragment : Fragment() {

    lateinit var mAdapter: FirestorePagingAdapter<User, UsersViewHolder>
    val database by lazy {
        FirebaseFirestore.getInstance()
            .collection("users")// use the same key, you used while creating the user in SignUpActivity
            .orderBy("name", Query.Direction.DESCENDING) // get the users in order by their names
    }
    val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)

        // Get a list of all the users and display them in the PeopleFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set up the adapter for the recyclerView which is inside fragment_chats
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }
    }

}

/**
 * You have 1000 users
 * 1 user -> 10kb
 * 10 * 1000 = 10000kb -> You may get a timeout
 * Pagination - getting data in pages
 */
