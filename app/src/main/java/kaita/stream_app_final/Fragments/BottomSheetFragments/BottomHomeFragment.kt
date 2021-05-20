package kaita.stream_app_final.Fragments.BottomSheetFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Modals.BetsPlaced
import kaita.stream_app_final.Activities.Modals.Options
import kaita.stream_app_final.Adapteres.*
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.chosen_Answer
import kaita.stream_app_final.AppConstants.Constants.selected_id
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_all_bets.view.*
import kotlinx.android.synthetic.main.fragment_bottom_home.view.*
import java.text.SimpleDateFormat
import java.util.*


class BottomHomeFragment : Fragment() {

    private lateinit var source : View
    private lateinit var mPagerAdapter: SectionPagerAdapter
    private lateinit var fragment: Fragment
    private lateinit var fm: FragmentManager
    private lateinit var ft: FragmentTransaction
    private lateinit var accessKey:String
    val options_Query = FirebaseChecker().homeRef_Streams.child(Constants.selected_id).child("options")
    val betsPlaced_Query = FirebaseChecker().homeRef_Streams.child(Constants.selected_id).child("bets")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        source = inflater.inflate(R.layout.fragment_bottom_home, container, false)
        initall()

        return  source
    }

    private fun initall() {

        getbundle_and_make_it_access_Key()
        load_Other_Features()
        initiate_Firebase_Recycler_View_Options()
        initiate_Firebase_Recycler_View_All_Bets_Made()
        listener_for_All_Bets_TextView()

    }

    private fun getbundle_and_make_it_access_Key() {
        val strtext = requireArguments().getString("key")
        accessKey = strtext.toString()
    }

    private fun listener_for_All_Bets_TextView() {
        FirebaseChecker().load_selected_Streamer_Bets(accessKey){
            val total_Count = it.childrenCount.toString()

             if(it.childrenCount.toInt() == 1) {
                source.view_all_bets_Made.setText("1 bet made")
            }else if (it.childrenCount < 0 ) {
                source.view_all_bets_Made.setText("No betters yet")
            }else if (it.childrenCount > 0) {
                 source.view_all_bets_Made.setText("View All  Bets $total_Count")
             }
        }

       source.view_all_bets_Made.setSafeOnClickListener {
           val bundle = Bundle()
           bundle.putString("key", accessKey)
           bundle.putString("key", accessKey)

           val fragobj = AllBetsFragment()
           fragobj.setArguments(bundle)
           val fragmentTransaction = parentFragmentManager.beginTransaction()
           fragmentTransaction.replace(R.id.fragment_container, fragobj).addToBackStack("Two")
           fragmentTransaction.commit()
       }
   }

   private fun initiate_Firebase_Recycler_View_All_Bets_Made() {
       source.recycler_view_Bets.setHasFixedSize(true)
       val mManager = WrappingRecyclerViewLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
       //mManager.setOrientation(RecyclerView.HORIZONTAL)
       mManager.isAutoMeasureEnabled = false
       source.recycler_view_Bets.isEnabled = false
       source.recycler_view_Bets.setLayoutManager(mManager)

       betsPlaced_Query.addValueEventListener(object: ValueEventListener {
           override fun onCancelled(error: DatabaseError) {
           }
           override fun onDataChange(snapshot: DataSnapshot) {
               if (!snapshot.exists() or !snapshot.hasChildren()) {
                   source.recycler_view_Bets.visibility = View.GONE
               } else {
                   source.recycler_view_Bets.visibility = View.VISIBLE
               }
           }
       })

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
               //requireActivity().makeLongToast(databaseError.toString())
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
        source.recycler_view_Bets.adapter = Constants.BetsPlaced_Adapter
    }

    private fun load_Other_Features() {
        FirebaseChecker().load_selected_Streamer_Stream(selected_id){
            if (it.exists() and it.hasChildren()) {
                val description = it.child("description").value.toString()
                val cashday = it.child("cashday").value.toString()
                val lastday = it.child("lastday").value.toString()
                val joinnumber = it.child("joinnumber").value.toString()
                val dpurl = it.child("dpurl").value.toString()
                val hostname = it.child("hostname").value.toString()
                val paid = it.child("paid").value.toString()
                val type = it.child("type").value.toString()
                val open: String = it.child("open:").value.toString()

                Log.d("ISSUE", lastday)

                //Get date and time from lastday
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                val final_last_day: Date = formatter.parse(lastday)
                val final_cash_day: Date = formatter.parse(cashday)

                source.bottom_actual_Description.setText(description)

                source.bottom_due_on.setText("Bet Expiry: $final_last_day")
                source.bottom_payment_on_text.setText("Cash Day $final_cash_day")

                if (type == "Closed") {
                    source.bottom_type.setText("Type: Closed - You can't add a betting option")
                    source.close_B.visibility == View.GONE
                } else if (type == "Open") {
                    source.bottom_type.setText("Type: Open - Enter your bet")
                }
                remove_Layout_If_Closed_Or_Open()
            }
        }
    }

    private fun remove_Layout_If_Closed_Or_Open() {
        FirebaseChecker().load_selected_Streamer_Stream(accessKey){
            val type = it.child("type").value.toString()
            if (type.equals("Closed")) {
                source.close_B.visibility = View.GONE
            }
        }
    }

    private fun initiate_Firebase_Recycler_View_Options() {

        source.recycler_view_options.setHasFixedSize(true)
        val numberOfColumns = 2
        val mManager = GridLayoutManager(requireContext(), numberOfColumns)
        mManager.orientation = RecyclerView.HORIZONTAL
        source.recycler_view_options.setLayoutManager(mManager)

        options_Query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    source.recycler_view_options.visibility = View.GONE
                }else {
                    source.recycler_view_options.visibility = View.VISIBLE
                }
            }
        })

        //Initialize FirebasePagingOptions
        Constants.options_Second = DatabasePagingOptions.Builder<Options>()
            .setLifecycleOwner(this)
            .setQuery(options_Query, Constants.config, Options::class.java)
            .build()
        loadFirebaseAdapter()
    }

    fun GridVertical() {
        source.recycler_view_options.setHasFixedSize(true)
        val numberOfColumns = 2
        source.recycler_view_options.setLayoutManager(GridLayoutManager(requireContext(), numberOfColumns))
    }
    fun GridHorizontal() {
        source.recycler_view_options.setHasFixedSize(true)
        val numberOfColumns = 2
        val mManager = GridLayoutManager(requireContext(), numberOfColumns)
        mManager.orientation = RecyclerView.HORIZONTAL
        source.recycler_view_options.setLayoutManager(mManager)
    }

    private fun loadFirebaseAdapter() {
        // Instantiate Paging Adapter
        Constants.mAdapter_Options = object : FirebaseRecyclerPagingAdapter<Options, OptionsViewHolder>(Constants.options_Second) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
                val view = layoutInflater.inflate(R.layout.options_display_layout, parent, false)
                return OptionsViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: OptionsViewHolder, position: Int, options: Options) {
                val databaseRerence = getRef(position)
                viewHolder.bind(options, databaseRerence, viewHolder, requireActivity(), source)
            }

            override fun onError(databaseError: DatabaseError) {
                //requireActivity().makeLongToast(databaseError.toString())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                }
            }
        }

        source.recycler_view_options.adapter = Constants.mAdapter_Options
    }

    override fun onStart() {
        super.onStart()
        Constants.mAdapter_Options.startListening();
        Constants.BetsPlaced_Adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if(Constants.mAdapter_Options != null) {
            Constants.mAdapter_Options.stopListening();
        }
        chosen_Answer = ""
    }

}