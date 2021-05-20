package kaita.stream_app_final.Activities.ProfileOperations

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.firebase.database.*
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState
import kaita.stream_app_final.Activities.Modals.Options
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.OptionsViewHolder
import kaita.stream_app_final.Adapteres.sendNotification
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.chosen_Answer
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_your_streams.*
import java.text.SimpleDateFormat
import java.util.*

class YourStreams : AppCompatActivity() {

    lateinit var callbackManager: CallbackManager
    lateinit var shareDialog: ShareDialog
    lateinit var options_Query: DatabaseReference
    lateinit var received_stamp: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_streams)
        initall()
    }

    private fun initall() {

        received_stamp=intent.getStringExtra("stamp")
        options_Query = FirebaseChecker().homeRef_Streams.child(received_stamp).child("options")

        initialize_Facebook()
        load_Active_Stream_Information()
        view_all_betters_text_Click()
        initiate_Firebase_Recycler_View_Options()
        listenerForCloseStream()
        shareStream.setOnClickListener {
            // Set up the alert builder
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("How do you want to share this to your friends?")
            builder.setPositiveButton("Others",
                DialogInterface.OnClickListener { dialog, which ->
                    FirebaseChecker().load_selected_Streamer_Stream(received_stamp){
                        if (it.exists()) {

                            var strAppLink = ""
                            val appPackageName = getPackageName();
                            strAppLink = try {
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            } catch (anfe: ActivityNotFoundException) {
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            }

                            val title = it.child("title").value.toString()
                            val mybet = it.child("contribution").value.toString()

                            var bettmessage = "Lets bet now !!\nTitle: $title\nMy Bet: $mybet\nGet Stream App: $strAppLink\nBet Link: https://www.worldstream.co.ke/streamed/joinbet.php?id=${received_stamp}"

                            val a = Intent(Intent.ACTION_SEND)
                            // this is the sharing part
                            a.type = "text/link"
                            val shareBody = bettmessage.trimIndent()
                            val shareSub = "Stream Bet"
                            a.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                            a.putExtra(Intent.EXTRA_TEXT, shareBody)
                            startActivity(Intent.createChooser(a, "Share Using"))

                        } else {
                            makeLongToast("Important information missing")
                        }
                    }
                    dialog.dismiss()
                })
            builder.setNegativeButton("Facebook",
                DialogInterface.OnClickListener { dialog, which ->
                    shareDialog.registerCallback(callbackManager, object :
                        FacebookCallback<Sharer.Result> {
                        override fun onSuccess(result: Sharer.Result?) {
                            makeLongToast("Your Stream was shared to Facebook")
                        }
                        override fun onCancel() {
                            makeLongToast("Sharing Cancelled")
                        }
                        override fun onError(error: FacebookException?) {
                            makeLongToast(error?.message.toString())
                        }
                    })

                    val linkContent = ShareLinkContent.Builder()
                        .setQuote("Facebook Share API Test Link - Random")
                        //.setContentUrl(Uri.parse("https://www.worldstream.co.ke/streamed/joinbet.php?id=${firebaseAuth.currentUser.uid}"))
                        .setContentUrl(Uri.parse("https://www.google.com/"))
                        .build()

                    if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                        shareDialog.show(linkContent)
                    } else {
                        makeLongToast("Ensure you have the Facebook App installed to share this Stream")
                    }
                    dialog.dismiss()
                })
            builder.setNeutralButton("Dismiss", null)
            // Create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }

    }

    private fun initialize_Facebook() {
        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
    }

    private fun listenerForCloseStream() {
        yourclosestream.setSafeOnClickListener {
            if (chosen_Answer == "") {
                showAlertDialog("You have to select the answer first")
            } else {
                //Let us play around with the time first
                FirebaseDatabase.getInstance().getReference().child("streams").child(received_stamp).child("cashday").addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error Occured: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val the_cash_day = snapshot.value.toString()

                            val simpleformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                            val current_day = Calendar.getInstance().getTime();
                            val thecurrent_day = simpleformat.format(current_day)

                            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                            val final_current_day: Date = formatter.parse(thecurrent_day)
                            val final_cash_day: Date = formatter.parse(the_cash_day)

                            if (final_cash_day.compareTo(final_current_day) > 0) {
                                showAlertDialog("You set the Cash Day on ${final_cash_day}, You can close this Stream only on or after this date, contact us for help")
                            } else {
                                //Insert the answer to db first
                                FirebaseChecker().homeRef_Streams.child(received_stamp).child("answer").setValue(
                                    chosen_Answer).addOnCompleteListener {
                                    if (it.isComplete) {
                                        FirebaseDatabase.getInstance().getReference().child("streams").child(firebaseAuth.currentUser.uid).child("stamp")
                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onCancelled(error: DatabaseError) {
                                                    makeLongToast(error.message)
                                                }
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    val one = snapshot.value.toString()
                                                    close_The_Stream(one, chosen_Answer)
                                                }
                                            })
                                    } else {
                                        showAlertDialog("Request incomplete: ${it.exception.toString()}")
                                    }
                                }
                            }

                        } else {
                            makeLongToast("The Stream was not set up correctly")
                        }
                    }
                })
            }
        }
    }

    private fun close_The_Stream(one: String,  chosenAnswer: String) {
        FirebaseDatabase.getInstance().getReference().child("manage").child(received_stamp).child("manage").setValue(
            received_stamp).addOnCompleteListener {
            if (it.isComplete) {
                FirebaseChecker().homeRef_Streams.child(received_stamp).child("remove").setValue("remove").addOnCompleteListener {
                    if (it.isComplete) {
                        sendNotification("admin", "Complete: User = ${received_stamp}")
                        showAlertDialog("Your Stream was closed, Stream will validate your answer and give you feedback")
                    }
                }
            } else {
                showAlertDialog(it.exception.toString())
            }
        }
    }


    private fun initiate_Firebase_Recycler_View_Options() {
        recycler_view_options_profile.setHasFixedSize(true)
        val numberOfColumns = 2
        val mManager = GridLayoutManager(this, numberOfColumns)
        mManager.orientation = RecyclerView.HORIZONTAL
        recycler_view_options_profile.setLayoutManager(mManager)

        options_Query.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists() or  !snapshot.hasChildren()) {
                    recycler_view_options_profile.visibility = View.GONE
                }else {
                    recycler_view_options_profile.visibility = View.VISIBLE
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

    private fun loadFirebaseAdapter() {
        // Instantiate Paging Adapter
        Constants.mAdapter_Options = object : FirebaseRecyclerPagingAdapter<Options, OptionsViewHolder>(Constants.options_Second) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
                val view = layoutInflater.inflate(R.layout.options_display_layout, parent, false)
                return OptionsViewHolder(view)
            }

            override fun onBindViewHolder(viewHolder: OptionsViewHolder, position: Int, options: Options) {
                val databaseRerence = getRef(position)
                viewHolder.bind(options, databaseRerence, viewHolder, this@YourStreams, null)
            }

            override fun onError(databaseError: DatabaseError) {
                //makeLongToast(databaseError.toString())
            }

            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {
                }
            }
        }

        recycler_view_options_profile.adapter = Constants.mAdapter_Options
    }

    private fun view_all_betters_text_Click() {
        yourallbets.setSafeOnClickListener {
            val intent = Intent(this, ActivityForViewingAllTheBetters::class.java)
            intent.putExtra("access",  received_stamp)
            startActivity(intent)
        }
    }

    private fun load_Active_Stream_Information() {

        FirebaseDatabase.getInstance().reference.child("ids").child(firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {

                            FirebaseDatabase.getInstance().reference.child("streams").child(received_stamp).addListenerForSingleValueEvent(object: ValueEventListener{
                                        override fun onCancelled(error: DatabaseError) {
                                            makeLongToast("Error: ${error.message}")
                                        }
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists() && snapshot.hasChildren()) {

                                                val title = snapshot.child("title").value.toString()
                                                val description = snapshot.child("description").value.toString()
                                                val cashday = snapshot.child("cashday").value.toString()
                                                val lastday = snapshot.child("lastday").value.toString()
                                                val type = snapshot.child("type").value.toString()
                                                val contribution = snapshot.child("contribution").value.toString()
                                                val numberOfBetters = ((snapshot.child("bets").childrenCount.toInt())).toString()

                                                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                                val actualcashday: Date = formatter.parse(cashday)
                                                val actuallastday: Date = formatter.parse(lastday)

                                                yourdescription.setText(description)
                                                youtitle.setText(title)
                                                yourdue.setText("Due: $actuallastday")
                                                yourpaymenton.setText("Payment $actualcashday")
                                                yourbetters.setText("Betters\n$numberOfBetters")
                                                yourContribution.setText("Your Bet:\nKes: $contribution")
                                                yourstreamtype.setText("Type:\n$type")

                                                total_amount_in_Stream(snapshot.child("contribution").value.toString().toInt())

                                            } else {
                                                makeLongToast("This stream is missing")
                                                finish()
                                            }
                                        }
                                    })
                        } else {
                            makeLongToast("You have no active streams")
                            finish()
                        }
                    }
                })
    }

    private fun total_amount_in_Stream(streamer_amount: Int) {
        var theamount_list = mutableListOf<Int>()
        FirebaseChecker().load_selected_Streamer_Bets(firebaseAuth.currentUser.uid) {
            for (value in it.children) {
                val amount_Placed  = value.child("bettamount").value.toString().toInt()
                theamount_list.add(amount_Placed)
            }
            val final_amount = (theamount_list.sum() + streamer_amount).toString()
            yourstreamamounttotal.setText("Stream Total\nKes: $final_amount")
        }
    }

    override fun onStart() {
        super.onStart()
        Constants.mAdapter_Options.startListening()
    }

    override fun onStop() {
        super.onStop()
        Constants.mAdapter_Options.stopListening()
    }
}