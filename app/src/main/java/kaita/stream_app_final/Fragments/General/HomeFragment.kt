package kaita.stream_app_final.Fragments.General

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Authentication.SignUpActivity
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.Activities.Normal.PostActivity
import kaita.stream_app_final.Adapteres.HomeFragmentViewHolder
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.alertDialog
import kaita.stream_app_final.AppConstants.Constants.config
import kaita.stream_app_final.AppConstants.Constants.mAdapter
import kaita.stream_app_final.AppConstants.Constants.options
import kaita.stream_app_final.AppConstants.Constants.streams
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.goToActivity_Unfinished
import kaita.stream_app_final.Extensions.showAlertDialog_Special
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(){

    private lateinit var source : View

    @Nullable
    override fun onCreateView( inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle? ): View? {
       source = inflater.inflate(R.layout.fragment_home, container, false)
        initall()
        return source
    }

    private fun initall() {

        set_Focus_To_Create_Stream_Button()
        alertDialog = AlertDialog.Builder(requireContext()).create()
        is_user_logged_In()
        initiate_Firebase_Recycler_View_Options()

        source.create_a_stream_button.setOnClickListener {
            requireActivity().goToActivity_Unfinished(requireActivity(), PostActivity::class.java)
        }
    }

    private fun set_Focus_To_Create_Stream_Button() {
        val button = source.create_a_stream_button
        button.setFocusable(true)
        button.setFocusableInTouchMode(true) ///add this line
        button.requestFocus()
    }

    private fun initiate_Firebase_Recycler_View_Options() {
        source.recycler_view.setHasFixedSize(true)
        val mManager = LinearLayoutManager(requireContext())
        source.recycler_view.setLayoutManager(mManager)

        streams.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    source.recycler_view.visibility = View.GONE
                }
            }
        })

        //Initialize FirebasePagingOptions
        options = DatabasePagingOptions.Builder<Post>()
            .setLifecycleOwner(this)
            .setQuery(streams, config, Post::class.java)
            .build()

        loadFirebaseAdapter()
    }

    private fun loadFirebaseAdapter() {
        // Instantiate Paging Adapter
        mAdapter = object : FirebaseRecyclerPagingAdapter<Post, HomeFragmentViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
                val view = requireActivity().layoutInflater.inflate(R.layout.firebase_load_options, parent, false)
                return HomeFragmentViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: HomeFragmentViewHolder, position: Int, post: Post) {
                val databaseRerence = getRef(position)
                viewHolder.bind(post, databaseRerence, viewHolder, requireActivity())
            }

            override fun onError(databaseError: DatabaseError) {
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    LoadingState.LOADING_INITIAL -> { source.swipeRefreshLayout.isRefreshing = true}
                    LoadingState.LOADING_MORE -> {source.swipeRefreshLayout.isRefreshing = true}
                    LoadingState.LOADED -> {source.swipeRefreshLayout.isRefreshing = false}
                    LoadingState.ERROR -> {
                        fun tryagain() {
                            source.swipeRefreshLayout.isRefreshing = true
                            mAdapter.retry()
                        }
                        requireActivity().showAlertDialog_Special(alertDialog, "Error", "We couldn't fetch the data", "Try Again",
                            ::tryagain)
                    }
                    LoadingState.FINISHED -> {
                        source.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }

        source.recycler_view.adapter = mAdapter
    }

    override fun onStart() {
        super.onStart()
        is_user_logged_In()
        mAdapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        if(mAdapter != null) {
            mAdapter.stopListening();
        }
        Constants.chosen_Answer = ""
    }

    private fun is_user_logged_In() {
        val user = Constants.firebaseAuth.currentUser
        if (user == null) {
            requireActivity().goToActivity(requireActivity(), SignUpActivity::class.java)
        }
    }
}