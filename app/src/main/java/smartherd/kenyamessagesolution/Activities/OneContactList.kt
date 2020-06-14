package smartherd.kenyamessagesolution.Activities

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_one_contact_list.*
import smartherd.kenyamessagesolution.Adapteres.Post
import smartherd.kenyamessagesolution.Adapteres.PostViewHolder
import smartherd.kenyamessagesolution.Extensions.goToActivity
import smartherd.kenyamessagesolution.R

class OneContactList : AppCompatActivity() {

    //FIrestore pagination tutorial and sample app used
    //https://medium.com/firebase-developers/firestore-pagination-in-android-using-firebaseui-library-1d7fe1a75704
    //https://github.com/PatilShreyas/FirestorePagingDemo-Android/blob/master/app/src/main/res/layout/activity_main.xml

    private lateinit var mAdapter: FirestorePagingAdapter<Post, PostViewHolder>
    private var auth = FirebaseAuth.getInstance()
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mPostsCollection = mFirestore.collection(auth.currentUser!!.email.toString()).document("Information")
    private val mQuery = mPostsCollection.collection("People")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_contact_list)

        // Init RecyclerView
        recyclerViewed.setHasFixedSize(true)
        recyclerViewed.layoutManager = LinearLayoutManager(this)

        setupAdapter()

        // Refresh Action on Swipe Refresh Layout
        swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }
    }

    private fun setupAdapter() {

        // Init Paging Configuration
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(10)
            .build()

        // Init Adapter Configuration
        val options = FirestorePagingOptions.Builder<Post>()
            .setLifecycleOwner(this)
            .setQuery(mQuery, config, Post::class.java)
            .build()

        // Instantiate Paging Adapter
        mAdapter = object : FirestorePagingAdapter<Post, PostViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                val view = layoutInflater.inflate(R.layout.onecardcontactlist, parent, false)
                return PostViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int, post: Post) {
                // Bind to ViewHolder
                viewHolder.bind(post)
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.e("MainActivity", e.message.toString())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADING_MORE -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    LoadingState.LOADED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.ERROR -> {
                        Toast.makeText(
                            applicationContext,
                            "Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                        swipeRefreshLayout.isRefreshing = false
                    }

                    LoadingState.FINISHED -> {
                        swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }

        // Finally Set the Adapter to RecyclerView
        recyclerViewed.adapter = mAdapter
    }

    override fun onBackPressed() {
        goToActivity(this, OneMainActivity::class.java)
    }
}