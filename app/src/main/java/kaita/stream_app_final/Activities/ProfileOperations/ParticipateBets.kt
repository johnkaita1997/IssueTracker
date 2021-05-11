package kaita.stream_app_final.Activities.ProfileOperations

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Modals.BetsPlaced_Final
import kaita.stream_app_final.Adapteres.BetsPlacedViewHolder_Final
import kaita.stream_app_final.Adapteres.WrappingRecyclerViewLayoutManager
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_participate_bets.*

class ParticipateBets : AppCompatActivity() {

    val betsPlaced_Query = FirebaseDatabase.getInstance().getReference().child("bets").child(firebaseAuth.currentUser.uid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participate_bets)
        initall()
    }

    private fun initall() {
        initiate_Firebase_Recycler_View_All_Bets_Made()
    }

    private fun initiate_Firebase_Recycler_View_All_Bets_Made() {
        recycler_participate_Bets.setHasFixedSize(true)
        val mManager = WrappingRecyclerViewLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //mManager.setOrientation(RecyclerView.HORIZONTAL)
        recycler_participate_Bets.setLayoutManager(mManager)

        betsPlaced_Query.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    recycler_participate_Bets.visibility = View.GONE
                }else {
                    recycler_participate_Bets.visibility = View.VISIBLE
                }
            }
        })

        //Initialize FirebasePagingOptions
        Constants.BetsPlaced_Paging_Final = DatabasePagingOptions.Builder<BetsPlaced_Final>()
            .setLifecycleOwner(this)
            .setQuery(betsPlaced_Query, Constants.config, BetsPlaced_Final::class.java)
            .build()

        loadFirebaseAdapter_Bets_Placed()
    }


    private fun loadFirebaseAdapter_Bets_Placed() {
        // Instantiate Paging Adapterb
        Constants.BetsPlaced_Adapter_Final = object : FirebaseRecyclerPagingAdapter<BetsPlaced_Final, BetsPlacedViewHolder_Final>(
            Constants.BetsPlaced_Paging_Final) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BetsPlacedViewHolder_Final {
                val view = layoutInflater.inflate(R.layout.list_item, parent, false)
                return BetsPlacedViewHolder_Final(view)
            }

            override fun onBindViewHolder(viewHolder: BetsPlacedViewHolder_Final, position: Int, betsplaced: BetsPlaced_Final) {
                val databaseRerence = getRef(position)
                viewHolder.bind(betsplaced, databaseRerence, viewHolder, this@ParticipateBets, null)
            }

            override fun onError(databaseError: DatabaseError) {
                //makeLongToast(databaseError.toString())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                }
            }
        }
        recycler_participate_Bets.adapter = Constants.BetsPlaced_Adapter_Final
    }
}