package kaita.stream_app_final.Fragments.General

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.*
import android.widget.PopupMenu
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.paypal.android.sdk.e
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import com.twigafoods.daraja.Daraja
import kaita.stream_app_final.Activities.Admin.Complains
import kaita.stream_app_final.Activities.Admin.DeleteReturn
import kaita.stream_app_final.Activities.Admin.ExpectingPaymentActivity
import kaita.stream_app_final.Activities.Admin.MoneyActivity
import kaita.stream_app_final.Adapteres.CustomCountryList
import kaita.stream_app_final.Adapteres.EndBet.EndBet
import kaita.stream_app_final.Adapteres.EndBet.EndBet_ViewHolder
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.PAYPAL_REQUEST_CODE
import kaita.stream_app_final.AppConstants.Constants.config
import kaita.stream_app_final.AppConstants.Constants.configd
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Extensions.goToActivity_Unfinished
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.view.*
import kotlinx.android.synthetic.main.fragment_notifications.view.list
import kotlinx.android.synthetic.main.fragment_profileoperations.view.*
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment : Fragment() {

    private lateinit var source : View
    private lateinit var thetoken : String
    private lateinit var daraja: Daraja
    var valueListener: ValueEventListener? = null
    lateinit var popup : PopupMenu
    private lateinit var menu: Menu

    private val countryNames = arrayOf(
        "Our Facebook Page",
        "Our Instagram Page",
        "Our Twitter Page"
    )
    private val imageid = arrayOf<Int>(
        R.drawable.facebookicon,
        R.drawable.instagramicon,
        R.drawable.twittericon
    )


    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        source =  inflater.inflate(R.layout.fragment_notifications, container, false)
        initall()
        return source
    }

    private fun initall() {
        setUp_connect_With_Us_List_View()
        dotted_Menu_Set_Up()
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

        operations_Button_Listener()

    }

    private fun setUp_connect_With_Us_List_View() {
        val listView = source.list
        val customCountryList = CustomCountryList(requireActivity(), countryNames, imageid)
        listView.adapter = customCountryList

        listView.setOnItemClickListener { parent, view, position, id ->
            val selected = countryNames[position]
            FirebaseDatabase.getInstance().reference.child("credentials").addListenerForSingleValueEvent(
                object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        requireActivity().makeLongToast("Error: ${error.message}")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val facebookpage = snapshot.child("facebookpage").value.toString()
                            val instagrampage = snapshot.child("instagrampage").value.toString()
                            val twitterpage = snapshot.child("twitterpage").value.toString()

                            when (selected) {
                                "Our Facebook Page" -> visit(facebookpage)
                                "Our Instagram Page" -> visit(instagrampage)
                                "Our Twitter Page" -> visit(twitterpage)
                                else -> {
                                    print("None")
                                }
                            }

                        } else {
                            requireActivity().makeLongToast("Video Url Credential Missing")
                        }
                    }
                })
        }
    }

    private fun visit(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun operations_Button_Listener() {
        source.adminoperations.setSafeOnClickListener {
            popup = PopupMenu(requireActivity(), it)
            popup.menu.add("Complains")
            popup.menu.add("Money")
            popup.menu.add("Delete | Return")
            popup.menu.add("Expecting Payment")
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    val entered_Category = item!!.title.toString()
                    if (entered_Category.equals("Complains")) {
                        requireActivity().goToActivity_Unfinished(
                            requireActivity(),
                            Complains::class.java
                        )
                    } else if (entered_Category.equals("Money")) {
                        requireActivity().goToActivity_Unfinished(
                            requireActivity(),
                            MoneyActivity::class.java
                        )
                    } else if (entered_Category.equals("Expecting Payment")) {
                        requireActivity().goToActivity_Unfinished(
                            requireActivity(),
                            ExpectingPaymentActivity::class.java
                        )
                    } else if (entered_Category.equals("Delete | Return")) {
                        requireActivity().goToActivity_Unfinished(
                            requireActivity(),
                            DeleteReturn::class.java
                        )
                    }
                    return false
                }
            })
            popup.show()
        }
    }

    private fun set_Visibility_Of_Recycler_View_To_Gone_If_Not_Admin() {
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("admin").exists()) {
                        source.finishTextView.visibility = View.VISIBLE
                        source.adminoperations.visibility = View.VISIBLE
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
                EndBet::class.java
            )
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

            override fun onBindViewHolder(
                viewHolder: EndBet_ViewHolder,
                position: Int,
                endBet: EndBet
            ) {
                val databaseRerence = getRef(position)
                viewHolder.bind(endBet, databaseRerence, viewHolder, requireActivity())
            }

            override fun onError(databaseError: DatabaseError) {
                //requireActivity().makeLongToast(databaseError.message.toString())
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
            complain_HashMap["complain"] = thecomplaint

            FirebaseDatabase.getInstance().getReference().child("complains").push().setValue(
                complain_HashMap
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    requireActivity().showAlertDialog("Thank you for reaching to us, we will get back to you over this issue.")
                    source.thecomplaint.setText("")

                    val amount = "10"
                    val description = "Stream Payment"
                    // Creating a paypal payment on below line.
                    val payment = PayPalPayment(
                        amount.toBigDecimal(),
                        "USD",
                        "Course Fees",
                        PayPalPayment.PAYMENT_INTENT_SALE
                    )
                    // Creating Paypal Payment activity intent
                    val intent = Intent(requireActivity(), PaymentActivity::class.java)
                    //putting the paypal configuration to the intent
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configd);
                    // Puting paypal payment to the intent
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                    // the request code will be used on the method onActivityResult
                    startActivityForResult(intent, PAYPAL_REQUEST_CODE);

                } else {
                    requireActivity().showAlertDialog(it.exception.toString())
                }
            }

        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val menuItem: MenuItem = menu.findItem(R.id.search)
        menuItem.setVisible(false)
    }

    private fun dotted_Menu_Set_Up() {
        setHasOptionsMenu(true);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {
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
}