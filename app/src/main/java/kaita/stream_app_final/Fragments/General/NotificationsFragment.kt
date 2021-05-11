package kaita.stream_app_final.Fragments.General

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import com.twigafoods.daraja.Daraja
import kaita.stream_app_final.Activities.Modals.EndBet
import kaita.stream_app_final.Adapteres.EndBet_ViewHolder
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment : Fragment() {

    private lateinit var source : View
    private lateinit var thetoken : String
    private lateinit var daraja: Daraja
    var valueListener: ValueEventListener? = null

    @Nullable
    override fun onCreateView( inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle? ): View? {
        source =  inflater.inflate(R.layout.fragment_notifications, container, false)
        initall()
        return source
    }

    private fun initall() {

        initiate_Firebase_Recycler_View_Options()

        source.submit_issue_Button.setSafeOnClickListener {
            val thecomplaint = source.thecomplaint.text.toString().trim()
            if (thecomplaint == "") {
                requireActivity().makeLongToast("Enter the message first")
            } else {
                sendComplaintToUs(thecomplaint)
            }
        }

        set_Visibility_Of_Recycler_View_To_Gone_If_Not_Admin()

    }

    private fun set_Visibility_Of_Recycler_View_To_Gone_If_Not_Admin() {
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("admin").exists()) {
                } else {
                    if (endView_Container != null && endView_Container.visibility == View.VISIBLE) {
                        endView_Container.visibility = View.GONE
                    }
                }
            }
        })
    }


    private fun initiate_Firebase_Recycler_View_Options() {
        source.recycler_view.setHasFixedSize(true)
        val mManager = LinearLayoutManager(requireContext())
        source.recycler_view.setLayoutManager(mManager)

        //Initialize FirebasePagingOptions
        Constants.options_End = DatabasePagingOptions.Builder<EndBet>()
            .setLifecycleOwner(this)
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("manage"),
                Constants.config,
                EndBet::class.java)
            .build()

        loadFirebaseAdapter()
    }



    private fun loadFirebaseAdapter() {
        // Instantiate Paging Adapter
        Constants.mAdapter_Options_End = object : FirebaseRecyclerPagingAdapter<EndBet, EndBet_ViewHolder>(
            Constants.options_End
        ) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EndBet_ViewHolder {
                val view = layoutInflater.inflate(R.layout.end_bet_viewholder_layout, parent, false)
                return EndBet_ViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: EndBet_ViewHolder, position: Int, endBet: EndBet) {
                val databaseRerence = getRef(position)
                viewHolder.bind(endBet, databaseRerence, viewHolder, requireActivity())
            }

            override fun onError(databaseError: DatabaseError) {
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                }
            }
        }

        source.recycler_view.adapter = Constants.mAdapter_Options_End
    }

    fun getTimestamp(): String? {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    }

    fun String.encode(): String {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
    }

    private fun sendComplaintToUs(thecomplaint: String) {

        FirebaseChecker().load_All {

            val name = it.child("name").value.toString()
            val mobile = it.child("mobileNumber").value.toString()
            val email = it.child("email").value.toString()

            val complain_HashMap = HashMap<String, String>()
            complain_HashMap["name"] = name
            complain_HashMap["mobile"] = mobile
            complain_HashMap["email"] = email

            FirebaseDatabase.getInstance().getReference().child("complains").child(firebaseAuth.currentUser.uid).push().setValue(complain_HashMap).addOnCompleteListener {
                if (it.isSuccessful) {
                    requireActivity().showAlertDialog("Thank you for reaching to us, we will get back to you over this issue.")
                    source.thecomplaint.setText("")
                } else {
                    requireActivity().showAlertDialog(it.exception.toString())
                }
            }

        }
    }
}