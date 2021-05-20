package kaita.stream_app_final.Activities.ProfileOperations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import kaita.stream_app_final.Adapteres.ShowStreamList.StreamList
import kaita.stream_app_final.Adapteres.ShowStreamList.StreamListViewHolder
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.AppConstants.Constants.streamList_Adapter
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_show_stream_list2.*

class ShowStreamList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_show_stream_list2)
        super.onCreate(savedInstanceState)
        initall()
    }

    private fun initall() {
        loadcategories()
    }

    private fun loadcategories() {

        categories_Recyclerview.setHasFixedSize(true)
        val mManager = LinearLayoutManager(this)
        categories_Recyclerview.setLayoutManager(mManager)

        val peopleReference: Query = FirebaseDatabase.getInstance().getReference()
            .child("ids")
            .child(firebaseAuth.currentUser.uid)
            .orderByChild("stamp")
        val options: FirebaseRecyclerOptions<StreamList?> = FirebaseRecyclerOptions.Builder<StreamList>()
            .setQuery(peopleReference, StreamList::class.java)
            .build()

       streamList_Adapter = object : FirebaseRecyclerAdapter<StreamList, StreamListViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StreamListViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_streamlistviewholder, parent, false)
                return StreamListViewHolder(view)
            }
            override fun onBindViewHolder(viewholder: StreamListViewHolder, position: Int, streamlist: StreamList) {
                viewholder.bind(streamlist, viewholder, this@ShowStreamList)
            }
        }

        categories_Recyclerview.adapter = streamList_Adapter
    }

    override fun onStart() {
        super.onStart()
        streamList_Adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (streamList_Adapter != null) {
            streamList_Adapter.stopListening();
        }
    }
}