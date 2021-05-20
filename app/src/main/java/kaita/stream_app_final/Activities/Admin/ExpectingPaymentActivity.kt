package kaita.stream_app_final.Activities.Admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import kaita.stream_app_final.Adapteres.Expectingpayment.ExpectingPaymented
import kaita.stream_app_final.Adapteres.Expectingpayment.ExpectingPaymentedViewHolder
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.expectionPaymentAdapter
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_expecting_payment.*

class ExpectingPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expecting_payment)
        initall()
    }

    private fun initall() {
        initiate_Firebase_Recycler_View_Options()
        searchListener()
    }

    private fun searchListener() {
        expecting_payment_Search_EditText.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchItem  = s.toString().trim()
                search(searchItem)
            }
        })
    }

    private fun search(searchItem: String) {
        if (searchItem == "") {
            if (expectionPaymentAdapter != null) {
                val query = FirebaseDatabase.getInstance().reference.child("allexpectingpayment");
                val options = FirebaseRecyclerOptions.Builder<ExpectingPaymented>()
                    .setQuery(query, ExpectingPaymented::class.java)
                    .build()
                expectionPaymentAdapter = object : FirebaseRecyclerAdapter<ExpectingPaymented, ExpectingPaymentedViewHolder>(options) {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ExpectingPaymentedViewHolder {
                        return ExpectingPaymentedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_expecting_payment, parent, false))
                    }
                    override fun onBindViewHolder(holder: ExpectingPaymentedViewHolder, position: Int, expectingpayment: ExpectingPaymented) {
                        val databaseRerence = getRef(position)
                        holder.bind(expectingpayment, holder, this@ExpectingPaymentActivity, databaseRerence)
                    }
                }
                expedting_Payment_RecyclerView.adapter = expectionPaymentAdapter
                expectionPaymentAdapter.startListening()
            }

        } else {
            if (expectionPaymentAdapter != null) {
                expectionPaymentAdapter.stopListening()
                val query = FirebaseDatabase.getInstance().reference
                    .child("allexpectingpayment")
                    .orderByChild("bettermobile")
                    .equalTo(searchItem)
                val options = FirebaseRecyclerOptions.Builder<ExpectingPaymented>()
                    .setQuery(query, ExpectingPaymented::class.java)
                    .build()
                expectionPaymentAdapter = object : FirebaseRecyclerAdapter<ExpectingPaymented, ExpectingPaymentedViewHolder>(options) {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ExpectingPaymentedViewHolder {
                        return ExpectingPaymentedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_expecting_payment, parent, false))
                    }
                    override fun onBindViewHolder(holder: ExpectingPaymentedViewHolder, position: Int, expectingpayment: ExpectingPaymented) {
                        val databaseRerence = getRef(position)
                        holder.bind(expectingpayment, holder, this@ExpectingPaymentActivity, databaseRerence)
                    }
                }
                expedting_Payment_RecyclerView.adapter = expectionPaymentAdapter
                expectionPaymentAdapter.startListening()
            }

        }
    }

    private fun initiate_Firebase_Recycler_View_Options() {
        expedting_Payment_RecyclerView.setHasFixedSize(true)
        val mManager = LinearLayoutManager(this)
        expedting_Payment_RecyclerView.setLayoutManager(mManager)

        FirebaseDatabase.getInstance().reference.child("allexpectingpayment").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                makeLongToast(error.message.toString())
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    makeLongToast("You have no expecting payments")
                    finish()
                }
            }
        })
        val query = FirebaseDatabase.getInstance().reference
            .child("allexpectingpayment")
            .equalTo("", "bettermobile")

        //Initialize FirebasePagingOptions
        Constants.options = DatabasePagingOptions.Builder<Post>()
            .setLifecycleOwner(this)
            .setQuery(query, Constants.config, Post::class.java)
            .build()

        loadFirebaseAdapter()
    }

    private fun loadFirebaseAdapter() {

        val query = FirebaseDatabase.getInstance().reference.child("allexpectingpayment");
        val options = FirebaseRecyclerOptions.Builder<ExpectingPaymented>()
            .setQuery(query, ExpectingPaymented::class.java)
            .build()

        expectionPaymentAdapter = object : FirebaseRecyclerAdapter<ExpectingPaymented, ExpectingPaymentedViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): ExpectingPaymentedViewHolder {
                return ExpectingPaymentedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_expecting_payment, parent, false))
            }
            override fun onBindViewHolder(holder: ExpectingPaymentedViewHolder, position: Int, expectingpayment: ExpectingPaymented) {
                val databaseRerence = getRef(position)
                holder.bind(expectingpayment, holder, this@ExpectingPaymentActivity, databaseRerence)
            }
        }

        expedting_Payment_RecyclerView.adapter = expectionPaymentAdapter
    }

    override fun onStart() {
        super.onStart()
        expectionPaymentAdapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        if (Constants.mAdapter != null) {
            expectionPaymentAdapter.stopListening();
        }
    }
}