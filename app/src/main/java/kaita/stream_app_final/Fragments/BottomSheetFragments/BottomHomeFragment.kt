package kaita.stream_app_final.Fragments.BottomSheetFragments

import android.app.Activity
import android.content.Intent
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.onesignal.OneSignal
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Modals.BetsPlaced
import kaita.stream_app_final.Activities.Modals.Options
import kaita.stream_app_final.Adapteres.*
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.chosen_Answer
import kaita.stream_app_final.AppConstants.Constants.selected_id
import kaita.stream_app_final.AppConstants.Constants.thebetamount
import kaita.stream_app_final.AppConstants.Constants.thedatabaseReference
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_bottom_home.view.*
import org.json.JSONException
import org.json.JSONObject
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
                val subject = it.child("title").value.toString()
                val cashday = it.child("cashday").value.toString()
                val lastday = it.child("lastday").value.toString()
                val joinnumber = it.child("joinnumber").value.toString()
                val dpurl = it.child("dpurl").value.toString()
                val hostname = it.child("hostname").value.toString()
                val paid = it.child("paid").value.toString()
                val patadate = it.child("postedon").value.toString()
                val type = it.child("type").value.toString()
                val open: String = it.child("open:").value.toString()

                Log.d("ISSUE", lastday)

                //Get date and time from lastday
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                val final_last_day: Date = formatter.parse(lastday)
                val final_cash_day: Date = formatter.parse(cashday)

                source.bottom_actual_Description.setText(description)
                source.subject_tview.setText(subject)

                source.bottom_due_on.setText("Bet Expiry: $final_last_day")
                source.bottom_payment_on_text.setText("Cash Day $final_cash_day")

                if (type == "Closed") {
                    //source.bottom_type.setText("Type: Closed - You can't add a betting option")
                    source.close_B.visibility == View.GONE
                } else if (type == "Open") {
                   // source.bottom_type.setText("Type: Open - Enter your bet")
                }

                // timestamp to Date

                source.bottom_type.setText("Posted On : $patadate")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PAYPAL_REQUEST_CODE) {
            // If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                // Getting the payment confirmation
                val confirm : PaymentConfirmation = data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                // if confirmation is not null
                if (confirm != null) {
                    try {
                        // Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4);
                        // on below line we are extracting json response and displaying it in a text view.
                        val payObj =  JSONObject(paymentDetails);
                        val payID = payObj.getJSONObject("response").getString("id");
                        val state = payObj.getJSONObject("response").getString("state");
                        requireActivity().makeLongToast("Payment " + state + "\n with payment id is " + payID)

                        finish_UP_Bet()

                    } catch (e : JSONException) {
                        // handling json exception on below line
                        requireActivity().makeLongToast(e.message.toString())
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // on below line we are checking the payment status.
                requireActivity().makeLongToast("Payment Cancelled")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // on below line when the invalid paypal config is submitted.
                requireActivity().makeLongToast("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
            }
        }
    }

    private fun finish_UP_Bet() {
        if (thedatabaseReference != null) {
            thedatabaseReference!!.setValue(Constants.hashMap_Selected_Bet_By_Better)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference()
                            .child("bets").child(
                                Constants.firebaseAuth.currentUser.uid
                            ).push().setValue(
                                Constants.hashMap_Selected_Bet_By_Better
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    if (Constants.progressDialog.isShowing) {
                                        Constants.progressDialog.dismiss()
                                    }
                                    requireActivity().showAlertDialog("Your bet was placed")
                                    chosen_Answer = ""
                                    sendNotification(selected_id, "One better joined bet")
                                    Constants.hashMap_Selected_Bet_By_Better.clear()
                                    Constants.thedatabaseReference.setValue(null)
                                    Constants.thebetamount = ""
                                    val reference =
                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(Constants.firebaseAuth.currentUser.uid)
                                            .child("betPay")
                                    reference.removeValue()
                                    FirebaseDatabase.getInstance().reference.child("keys")
                                        .child(
                                            selected_id
                                        ).addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onCancelled(error: DatabaseError) {
                                                requireActivity().makeLongToast("Error: ${error.message}")
                                            }

                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    val actual_id =
                                                        snapshot.value.toString()
                                                    if (actual_id.equals(
                                                            Constants.firebaseAuth.currentUser.uid.toString()
                                                        )
                                                    ) {
                                                        FirebaseDatabase.getInstance().reference.child(
                                                            "streams"
                                                        ).child(selected_id).child(
                                                            "contribution"
                                                        ).setValue(thebetamount)
                                                    }
                                                } else {
                                                    requireActivity().makeLongToast("Error!, missing info.")
                                                }
                                            }
                                        })

                                    register_To_OneSignal()

                                } else {
                                    requireActivity().makeLongToast("An error occured")
                                }
                            }
                    } else {
                        requireActivity().showAlertDialog("Failed to place bet, ${it.exception.toString()}")
                        Constants.hashMap_Selected_Bet_By_Better.clear()
                        if (Constants.progressDialog.isShowing) {
                            Constants.progressDialog.dismiss()
                        }
                    }
                }
        } else {
            requireActivity().makeLongToast("Something went wrong")
        }
    }

    private fun register_To_OneSignal() {
        OneSignal.sendTag(selected_id, selected_id)
    }
}