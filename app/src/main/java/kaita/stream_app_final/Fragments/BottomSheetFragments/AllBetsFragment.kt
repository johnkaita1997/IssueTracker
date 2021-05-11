package kaita.stream_app_final.Fragments.BottomSheetFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Modals.BetsPlaced
import kaita.stream_app_final.Adapteres.BetsPlacedViewHolder
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.WrappingRecyclerViewLayoutManager
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_all_bets.view.*

class AllBetsFragment : Fragment() {

    private lateinit var source : View
    private lateinit var accessKey:String
    val betsPlaced_Query = FirebaseChecker().homeRef_Streams.child(Constants.selected_id).child("bets")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        source  = inflater.inflate(R.layout.fragment_all_bets, container, false)
        initall()
        return source
    }

    private fun initall() {
        getbundle_and_make_it_access_Key()
        initiate_Firebase_Recycler_View_All_Bets_Made()
    }

    private fun getbundle_and_make_it_access_Key() {
        val strtext = requireArguments().getString("key")
        accessKey = strtext.toString()

    }

    private fun initiate_Firebase_Recycler_View_All_Bets_Made() {
        source.recycler_view_BetsAll.setHasFixedSize(true)
        val mManager = WrappingRecyclerViewLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        //mManager.setOrientation(RecyclerView.HORIZONTAL)
        source.recycler_view_BetsAll.setLayoutManager(mManager)

        //Initialize FirebasePagingOptions
        Constants.BetsPlaced_Paging = DatabasePagingOptions.Builder<BetsPlaced>()
            .setLifecycleOwner(this)
            .setQuery(betsPlaced_Query, Constants.config, BetsPlaced::class.java)
            .build()

        loadFirebaseAdapter_Bets_Placed()
    }


    private fun loadFirebaseAdapter_Bets_Placed() {
        // Instantiate Paging Adapter
        Constants.BetsPlaced_Adapter = object : FirebaseRecyclerPagingAdapter<BetsPlaced, BetsPlacedViewHolder>(Constants.BetsPlaced_Paging) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetsPlacedViewHolder {
                val view = layoutInflater.inflate(R.layout.betsplaced_layout, parent, false)
                return BetsPlacedViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: BetsPlacedViewHolder, position: Int, betsplaced: BetsPlaced) {
                val databaseRerence = getRef(position)
                viewHolder.bind(betsplaced, databaseRerence, viewHolder, requireActivity(), source)
            }

            override fun onError(databaseError: DatabaseError) {
               // requireActivity().makeLongToast(databaseError.toString())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                    /*LoadingState.LOADING_INITIAL -> { source.swipeRefreshLayout_Options.isRefreshing = true}
                     LoadingState.LOADING_MORE -> {source.swipeRefreshLayout_Options.isRefreshing = true}
                     LoadingState.LOADED -> {source.swipeRefreshLayout_Options.isRefreshing = false}
                     LoadingState.ERROR -> {requireActivity().makeLongToast("Refresh error occured")
                         source.swipeRefreshLayout_Options.isRefreshing = false
                         Constants.mAdapter_Options.retry();
                     }
                     LoadingState.FINISHED -> {
                         source.swipeRefreshLayout_Options.isRefreshing = false
                     }*/
                }
            }
        }
        source.recycler_view_BetsAll.adapter = Constants.BetsPlaced_Adapter
    }

}