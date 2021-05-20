package kaita.stream_app_final.Activities.ProfileOperations

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Modals.BetsPlaced
import kaita.stream_app_final.Adapteres.BetsPlacedViewHolder
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.WrappingRecyclerViewLayoutManager
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_view_all_betters.*

class ViewAllBetters : AppCompatActivity() {

    val betsPlaced_Query = FirebaseChecker().homeRef_Streams.child(firebaseAuth.currentUser.uid).child("bets")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_betters)

        initall()
    }

    private fun initall() {
        initiate_Firebase_Recycler_View_All_Bets_Made()
    }

    private fun initiate_Firebase_Recycler_View_All_Bets_Made() {
        recycler_view_BetsAllu.setHasFixedSize(true)
        val mManager = WrappingRecyclerViewLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //mManager.setOrientation(RecyclerView.HORIZONTAL)
        mManager.isAutoMeasureEnabled = false
        recycler_view_BetsAllu.isEnabled = false
        recycler_view_BetsAllu.setLayoutManager(mManager)


        betsPlaced_Query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    recycler_view_BetsAllu.visibility = View.GONE
                }else {
                    recycler_view_BetsAllu.visibility = View.VISIBLE
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
                viewHolder.bind(betsplaced, databaseRerence, viewHolder, this@ViewAllBetters, null)
            }

            override fun onError(databaseError: DatabaseError) {
                //makeLongToast(databaseError.toString())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                }
            }
        }
        recycler_view_BetsAllu.adapter = Constants.BetsPlaced_Adapter
    }

    override fun onStart() {
        super.onStart()
        Constants.BetsPlaced_Adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        Constants.BetsPlaced_Adapter.stopListening()
    }
}