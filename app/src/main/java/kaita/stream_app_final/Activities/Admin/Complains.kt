package kaita.stream_app_final.Activities.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.Adapteres.Complaints.ComplainViewHolder
import kaita.stream_app_final.Adapteres.Complaints.Complained
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.complainsAdapter
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_complains.*

class Complains : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complains)
        initall()
    }

    private fun initall() {
        initiate_Firebase_Recycler_View_Options()
    }

    private fun initiate_Firebase_Recycler_View_Options() {

        complains_RecyclerView.setHasFixedSize(true)
        val mManager = LinearLayoutManager(this)
        complains_RecyclerView.setLayoutManager(mManager)

        FirebaseDatabase.getInstance().reference.child("complains").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    makeLongToast("You have no complains")
                    finish()
                }
            }
        })

        //Initialize FirebasePagingOptions
        Constants.options = DatabasePagingOptions.Builder<Post>()
            .setLifecycleOwner(this)
            .setQuery(Constants.streams, Constants.config, Post::class.java)
            .build()

        loadFirebaseAdapter()
    }

    private fun loadFirebaseAdapter() {

        val query = FirebaseDatabase.getInstance().reference.child("complains");
        val options = FirebaseRecyclerOptions.Builder<Complained>()
            .setQuery(query, Complained::class.java)
            .build()

       complainsAdapter = object : FirebaseRecyclerAdapter<Complained, ComplainViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ComplainViewHolder {
                return ComplainViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_complains, parent, false))
            }
            override fun onBindViewHolder(holder: ComplainViewHolder, position: Int, complain: Complained) {
                val databaseRerence = getRef(position)
                holder.bind(complain, holder, this@Complains, databaseRerence)
            }
        }

        complains_RecyclerView.adapter =complainsAdapter
    }

    override fun onStart() {
        super.onStart()
       complainsAdapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        if (Constants.mAdapter != null) {
           complainsAdapter.stopListening();
        }
    }

}