package kaita.stream_app_final.Fragments.General

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.database.*
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import kaita.stream_app_final.Activities.Authentication.SignUpActivity
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.Activities.Normal.PostActivity
import kaita.stream_app_final.Adapteres.Categories.Category
import kaita.stream_app_final.Adapteres.Categories.CategoryViewHolder
import kaita.stream_app_final.Adapteres.HomeFragmentViewHolder
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.alertDialog
import kaita.stream_app_final.AppConstants.Constants.categories_Adapter
import kaita.stream_app_final.AppConstants.Constants.config
import kaita.stream_app_final.AppConstants.Constants.database
import kaita.stream_app_final.AppConstants.Constants.mAdapter
import kaita.stream_app_final.AppConstants.Constants.options
import kaita.stream_app_final.AppConstants.Constants.streams
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.goToActivity_Unfinished
import kaita.stream_app_final.Extensions.makeLongToast
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
        loadcategories()
        alertDialog = AlertDialog.Builder(requireContext()).create()
        is_user_logged_In()
        initiate_Firebase_Recycler_View_Options()

        source.create_a_stream_button.setOnClickListener {
            requireActivity().goToActivity_Unfinished(requireActivity(), PostActivity::class.java)
        }
    }

    private fun loadcategories() {
        source.categoris_recyclerView.setHasFixedSize(true)
        val numberOfColumns = 1
        val mManager = GridLayoutManager(requireActivity(), numberOfColumns)
        mManager.orientation = RecyclerView.HORIZONTAL
        source.categoris_recyclerView.setLayoutManager(mManager)

        val peopleReference: Query = FirebaseDatabase.getInstance()
            .getReference()
            .child("categories")

        val options: FirebaseRecyclerOptions<Category?> = FirebaseRecyclerOptions.Builder<Category>()
            .setQuery(peopleReference, Category::class.java)
            .build()

        categories_Adapter = object : FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_categories, parent, false)
                return CategoryViewHolder(view)
            }
            override fun onBindViewHolder(viewholder: CategoryViewHolder, position: Int, person: Category) {
                viewholder.bind(person, viewholder, requireActivity())
            }
        }
        source.categoris_recyclerView.adapter = categories_Adapter
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

        val config = PagedList.Config.Builder()
            .setInitialLoadSizeHint(5)
            .setEnablePlaceholders(false)
            .setPrefetchDistance(2)
            .setPageSize(3) //If you scroll down to page number 5 it will load 3 more items
            .build()

        val query = database.collection("streams").orderBy("stamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
        val options = FirestorePagingOptions.Builder<Post>()
            .setQuery(query, config, Post::class.java)
            .build()

        mAdapter = object : FirestorePagingAdapter<Post, HomeFragmentViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup,viewType: Int ): HomeFragmentViewHolder{
                return HomeFragmentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.firebase_load_options, parent, false))
            }

            override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int, post: Post) {
                holder.bind(post, holder, requireActivity())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                super.onLoadingStateChanged(state)
                when (state) {
                    LoadingState.LOADING_INITIAL -> { source.swipeRefreshLayout_Optionswow.isRefreshing = true}
                    LoadingState.LOADING_MORE -> {source.swipeRefreshLayout_Optionswow.isRefreshing = true}
                    LoadingState.LOADED -> {source.swipeRefreshLayout_Optionswow.isRefreshing = false}
                    LoadingState.ERROR -> {requireActivity().makeLongToast("Refresh error occured")
                        source.swipeRefreshLayout_Optionswow.isRefreshing = false
                        Constants.mAdapter_Options.retry();
                    }
                    LoadingState.FINISHED -> {
                        source.swipeRefreshLayout_Optionswow.isRefreshing = false
                        if(getItemCount() == 0) {
                            fun goToCreate_Stream() {
                                requireActivity().goToActivity_Unfinished(requireActivity(), PostActivity::class.java)
                            }
                            if (!alertDialog.isShowing) {
                                //requireActivity().showAlertDialog_Special( alertDialog,"No Data","No matching streams, would you like to create one","Proceed",::goToCreate_Stream)
                                requireActivity().makeLongToast("No matching streams.")
                            }
                        }
                    }
                }
            }
        }


        source.swipeRefreshLayout_Optionswow.setOnRefreshListener {
            source.swipeRefreshLayout_Optionswow.isRefreshing = true
            val query = database.collection("streams").orderBy("stamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            val options = FirestorePagingOptions.Builder<Post>()
                .setQuery(query, Constants.config, Post::class.java)
                .build()
            // Change options of adapter.
            mAdapter.updateOptions(options)
            source.swipeRefreshLayout_Optionswow.isRefreshing = false
        }

        source.recycler_view.adapter = mAdapter

    }

    override fun onStart() {
        super.onStart()
        is_user_logged_In()
        mAdapter.startListening();
        categories_Adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
        if (categories_Adapter != null) {
            categories_Adapter.stopListening();
        }
        Constants.chosen_Answer = ""
    }

    private fun is_user_logged_In() {
        val user = Constants.firebaseAuth.currentUser
        if (user == null) {
            requireActivity().goToActivity(requireActivity(), SignUpActivity::class.java)
        }
    }

    fun callAboutUsActivity(text: String) {
        val incoming_Text = text
        if (incoming_Text != "") {
            val query = database
                .collection("streams")
                .whereGreaterThanOrEqualTo("title", text.capitalize())
                .whereLessThanOrEqualTo("title",  "${text.capitalize()}\uf8ff")
            val options = FirestorePagingOptions.Builder<Post>()
                .setQuery(query, config, Post::class.java)
                .build()
            // Change options of adapter.
            mAdapter.updateOptions(options)
        } else {
            val query = database.collection("streams");
            val options = FirestorePagingOptions.Builder<Post>()
                .setQuery(query, config, Post::class.java)
                .build()
            // Change options of adapter.
            mAdapter.updateOptions(options)
        }
    }
}