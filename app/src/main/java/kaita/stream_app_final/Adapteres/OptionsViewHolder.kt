package kaita.stream_app_final.Adapteres

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.twigafoods.daraja.Daraja
import com.twigafoods.daraja.DarajaListener
import com.twigafoods.daraja.model.AccessToken
import com.twigafoods.daraja.model.LNMExpress
import com.twigafoods.daraja.model.LNMResult
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Modals.Options
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.alphabetical_List
import kaita.stream_app_final.AppConstants.Constants.chosen_Answer
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.AppConstants.Constants.hashMap_Selected_Bet_By_Better
import kaita.stream_app_final.AppConstants.Constants.progressDialog
import kaita.stream_app_final.AppConstants.Constants.selected_id
import kaita.stream_app_final.Extensions.alert
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class OptionsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var optionsDisplayName: TextView = itemView.findViewById(R.id.options_display_name)
    private var optionsDisplayValue: TextView = itemView.findViewById(R.id.options_display_Value)
    private lateinit var daraja: Daraja
    var valueListener: ValueEventListener? = null
    private var thedatabaseReference: DatabaseReference? = null
    private var thebettername = ""
    private var thebettamount = ""
    private lateinit var place_your_bet_open_edt: EditText
    private lateinit var betamount: EditText

    fun bind(options: Options,databaseReference: DatabaseReference,viewHolder: OptionsViewHolder,theactivity: FragmentActivity,source: View?) {

        initiate_Daraje(theactivity, source)
        start_Listener_For_Payment(theactivity, source)

        progressDialog = SpotsDialog.Builder().setContext(theactivity).build() as SpotsDialog

        val position = adapterPosition
        val selected_User = databaseReference.ref
        val key = selected_User.key
        optionsDisplayName.text = key
        optionsDisplayValue.text = options.name
        optionsDisplayName.tag = key

        optionsDisplayName.setSafeOnClickListener {
            //val selected_User = databaseReference.ref
            //val key = selected_User.key
            val thetext = it.tag.toString().toLowerCase()
            hashMap_Selected_Bet_By_Better["streamoption"] = thetext
            chosen_Answer = thetext
            theactivity.showAlertDialog("Option ${optionsDisplayName.text.toString().toUpperCase()} has been set.")
        }

         var submit_Button: Button? = source?.findViewById(R.id.thebet_submition)

        submit_Button?.setSafeOnClickListener {

            //Check if the user is logged and stuff like that
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    theactivity.makeLongToast(error.message)
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        if (!snapshot.child("name").exists()) {
                            theactivity.makeLongToast("Set your name in the profile page to continue")
                        } else if (!snapshot.child("mobileNumber").exists()) {
                            theactivity.makeLongToast("Set your mobile Number in the profile page to continue")
                        } else if (!snapshot.child("email").exists()) {
                            theactivity.makeLongToast("Set your email in the profile page to continue")
                        }else if (!snapshot.child("dpurl").exists()) {
                            theactivity.makeLongToast("Set up your profile picture in profile page to continue")
                        }else if (!snapshot.child("idnumber").exists()) {
                            theactivity.makeLongToast("Set up your Id Number in profile page to continue")
                        } else {
                            proceed()
                        }

                    } else {
                        theactivity.makeLongToast("Your personal profile is not set")
                    }
                }

                private fun proceed() {
                    //Let us play around with the time first
                    FirebaseDatabase.getInstance().getReference().child("streams").child(Constants.selected_id).child("lastday").addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            theactivity.makeLongToast("Error Occured: ${error.message.toString()}")
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val thelast_day = snapshot.value.toString()

                                val simpleformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                                val current_day = Calendar.getInstance().getTime();
                                val thecurrent_day = simpleformat.format(current_day)

                                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                                val final_current_day: Date = formatter.parse(thecurrent_day)
                                val final_last_day: Date = formatter.parse(thelast_day)

                                if (final_last_day.compareTo(final_current_day) < 0) {
                                    theactivity.showAlertDialog("The voting window for this bet has ended")
                                } else {
                                    place_your_bet_open_edt = source?.findViewById(R.id.place_your_bet_open_edt)!!

                                    if (!progressDialog.isShowing) { progressDialog.show()}

                                    FirebaseDatabase.getInstance().getReference().child("streams").child(Constants.selected_id).child("bets").child(
                                        firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(object: ValueEventListener{
                                        override fun onCancelled(error: DatabaseError) {
                                            if (progressDialog.isShowing) {progressDialog.dismiss()}
                                        }
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                theactivity.showAlertDialog("You already placed your bet")
                                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                            } else {
                                                val user_Option = place_your_bet_open_edt?.text.toString().trim()

                                                betamount = source?.findViewById(R.id.amount_placed_for_the_bet)
                                                var actual_bet_Amount = betamount?.text.toString().trim()

                                                if (actual_bet_Amount == "" || actual_bet_Amount == "None") {
                                                    theactivity.showAlertDialog("You have to enter an amount first")
                                                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                } else {
                                                    //Check it is not full
                                                    databaseReference.parent!!.parent!!.child("joinnumber").addListenerForSingleValueEvent(object : ValueEventListener{
                                                        override fun onCancelled(error: DatabaseError) {
                                                            if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                            theactivity.makeLongToast(error.message)
                                                        }
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                val full_Number = snapshot.value.toString().toInt()
                                                                databaseReference.parent!!.parent!!.child("bets").addListenerForSingleValueEvent(object: ValueEventListener{
                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                        theactivity.makeLongToast(error.message)
                                                                    }
                                                                    override fun onDataChange(snapshot: DataSnapshot) {

                                                                        FirebaseChecker().load_All {

                                                                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                                            val currentDate = sdf.format(Date())

                                                                            val bettername = it.child("name").value.toString()
                                                                            val betterimage = it.child("dpurl").value.toString()
                                                                            val bettermobile = it.child("mobileNumber").value.toString()
                                                                            val betteremail = it.child("email").value.toString()
                                                                            val betterid = firebaseAuth.uid.toString()
                                                                            val betteridnumber = it.child("idnumber").value.toString()
                                                                            val streamid = Constants.selected_id
                                                                            val bettamount  = actual_bet_Amount

                                                                            //Load the title using the Stream Id.
                                                                            FirebaseChecker().load_selected_Streamer_Stream(streamid) {

                                                                                val thetittle = it.child("title").value.toString()
                                                                                val stamp = it.child("stamp").value.toString()

                                                                                hashMap_Selected_Bet_By_Better["betttittle"] = thetittle
                                                                                hashMap_Selected_Bet_By_Better["stamp"] = stamp
                                                                                hashMap_Selected_Bet_By_Better["betterimage"] = betterimage
                                                                                hashMap_Selected_Bet_By_Better["bettermobile"] = bettermobile
                                                                                hashMap_Selected_Bet_By_Better["betteremail"] = betteremail
                                                                                hashMap_Selected_Bet_By_Better["betterid"] = betterid
                                                                                hashMap_Selected_Bet_By_Better["betteridnumber"] = betteridnumber
                                                                                hashMap_Selected_Bet_By_Better["streamid"] = streamid
                                                                                hashMap_Selected_Bet_By_Better["bettamount"] = bettamount
                                                                                hashMap_Selected_Bet_By_Better["bettdate"] = currentDate
                                                                                hashMap_Selected_Bet_By_Better["bettstate"] = "Open"
                                                                                hashMap_Selected_Bet_By_Better["bettername"] = bettername

                                                                                if (user_Option != "") {
                                                                                    //Check for the maximum number of available options for this application
                                                                                    databaseReference.parent!!.parent!!.child("maxoptions").addListenerForSingleValueEvent(object : ValueEventListener{
                                                                                        override fun onCancelled(error: DatabaseError) {
                                                                                            if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                        }
                                                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                                                            if (snapshot.exists()) {
                                                                                                val maximum_Number = snapshot.value.toString().toInt()

                                                                                                databaseReference.parent!!.parent!!.child("options").addListenerForSingleValueEvent(object : ValueEventListener{
                                                                                                    override fun onCancelled(error: DatabaseError) {
                                                                                                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                                    }
                                                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                                                        if (snapshot.exists() && snapshot.childrenCount > maximum_Number) {
                                                                                                            theactivity.showAlertDialog( "Maximum number of options reached for this Stream")
                                                                                                            if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                                        } else {
                                                                                                            alert = AlertDialog.Builder(theactivity)
                                                                                                                .setTitle("Stream")
                                                                                                                .setCancelable(false)
                                                                                                                .setMessage("Would you like to bet on the option you entered?")
                                                                                                                .setIcon(R.drawable.mainicon)
                                                                                                                .setPositiveButton("Yes",
                                                                                                                    DialogInterface.OnClickListener { dialog, _ ->
                                                                                                                        val middleList = mutableListOf<String>()
                                                                                                                        databaseReference.parent!!.parent!!.child("options").addListenerForSingleValueEvent(object :ValueEventListener {
                                                                                                                            override fun onCancelled(error: DatabaseError) {
                                                                                                                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                                                            }
                                                                                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                                                                                if (!snapshot.exists()) {
                                                                                                                                    databaseReference.parent!!.parent!!.child("options")
                                                                                                                                        .child("a").child("name")
                                                                                                                                        .setValue(user_Option)
                                                                                                                                } else {
                                                                                                                                    for (child in snapshot.children) {
                                                                                                                                        middleList.add(child.key.toString())
                                                                                                                                    }
                                                                                                                                    if (!middleList.isEmpty()) {
                                                                                                                                        for (value in alphabetical_List) {
                                                                                                                                            if (value in middleList) {
                                                                                                                                            } else {
                                                                                                                                                databaseReference.parent!!.parent!!.child("options").child(value).child("name").setValue(user_Option)
                                                                                                                                                hashMap_Selected_Bet_By_Better["streamoption"] = value
                                                                                                                                                break
                                                                                                                                            }
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }

                                                                                                                                thedatabaseReference = databaseReference.parent!!.parent!!.child("bets").child(betterid)
                                                                                                                                thebettamount = bettamount
                                                                                                                                thebettername = bettername

                                                                                                                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                                                                make_Mpesa_Request(theactivity, source, bettamount, bettername, bettermobile)

                                                                                                                            }
                                                                                                                        })
                                                                                                                    })

                                                                                                                .setNegativeButton("No",
                                                                                                                    DialogInterface.OnClickListener { dialog, _ ->
                                                                                                                        dialog.dismiss()
                                                                                                                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                                                    })
                                                                                                                .show()
                                                                                                        }
                                                                                                    }
                                                                                                })

                                                                                                theactivity.showAlertDialog("Very important betting profile information missing")
                                                                                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                            }
                                                                                        }
                                                                                    })
                                                                                } else {
                                                                                    if (hashMap_Selected_Bet_By_Better == null || hashMap_Selected_Bet_By_Better["streamoption"] == "" || hashMap_Selected_Bet_By_Better["streamoption"] == null) {
                                                                                        theactivity.showAlertDialog("You have to select at least one betting option or enter your own")
                                                                                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                    } else {

                                                                                        thedatabaseReference = databaseReference.parent!!.parent!!.child("bets").child(betterid)
                                                                                        thebettamount = bettamount
                                                                                        thebettername = bettername

                                                                                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                                                        make_Mpesa_Request(theactivity, source, bettamount, bettername, bettermobile)

                                                                                    }
                                                                                }

                                                                            }
                                                                        }
                                                                    }
                                                                })

                                                            } else {
                                                                theactivity.makeLongToast("Bett Profile Missing")
                                                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                            }
                                                        }
                                                    })
                                                }
                                            }
                                        }
                                    })
                                }

                            } else {
                                theactivity.makeLongToast("The Stream was not set up correctly")
                            }
                        }
                    })
                }
            })
        }
    }

    private fun make_Mpesa_Request(theactivity: FragmentActivity, source: View, bettamount: String, bettername: String, bettermobile: String) {

        FirebaseDatabase.getInstance().reference.child("credentials").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                theactivity.makeLongToast(error.message)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    if (snapshot.child("busienessshortcode").exists() && snapshot.child("passkey").exists()&& snapshot.child("callback").exists()) {

                        val busienessshortcode = snapshot.child("busienessshortcode").value.toString()
                        val thepasskey = snapshot.child("passkey").value.toString()
                        val thecallback = snapshot.child("callback").value.toString()

                        val busienessShortcode = busienessshortcode
                        val mobileNumber = bettermobile
                        val partyA = mobileNumber
                        val partyB = busienessShortcode
                        val callback = "https://worldstream.co.ke/streamed/confirmation.php?id=${firebaseAuth.currentUser.uid}"
                        val accountReference = "$bettername"
                        val transactionDesc = "Bid-Placed"
                        val amount = bettamount
                        val passkey = thepasskey

                        val lnmExpress = LNMExpress(
                            busienessShortcode,
                            passkey,
                            amount,
                            partyA,
                            partyB,
                            mobileNumber,
                            callback,
                            accountReference,
                            transactionDesc
                        )

                        daraja.requestMPESAExpress(lnmExpress, object : DarajaListener<LNMResult> {
                            override fun onResult(lnmResult: LNMResult) {
                                theactivity.showAlertDialog("You will receive an Mpesa prompt shortly")
                            }

                            override fun onError(error: String) {
                                theactivity.showAlertDialog("Mpesa Failed: ${error}")
                                Log.d("Pesa", "onError: $error")
                                if (progressDialog.isShowing) {
                                    progressDialog.dismiss()
                                }
                            }
                        }
                        )

                    }else {
                        theactivity.makeLongToast("Admin info missing")
                    }

                    val currentTimestamp = System.currentTimeMillis()
                    val c = Calendar.getInstance().time
                    println("Current time => $c")
                    val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                    val formattedDate = df.format(c)
                    val paymentList = HashMap<String, Any>()
                    paymentList["name"] = bettername
                    paymentList["amount"] = bettamount
                    paymentList["mobile"] = bettermobile
                    paymentList["datemade"] = formattedDate
                    //Expectation
                    FirebaseDatabase.getInstance().reference.child("expectingpayment").child(currentTimestamp.toString()).setValue(paymentList)
                    FirebaseDatabase.getInstance().reference.child("expectingpayment").child(currentTimestamp.toString()).push().setValue(hashMap_Selected_Bet_By_Better)

                } else {
                    theactivity.makeLongToast("Admin info missing")
                }
            }
        })
    }

    private fun initiate_Daraje(theactivity: FragmentActivity,source: View?) {

        FirebaseDatabase.getInstance().reference.child("credentials").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                theactivity.makeLongToast(error.message)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    if (snapshot.child("consumerkey").exists() && snapshot.child("consumersecret").exists()) {

                        val consumerkey = snapshot.child("consumerkey").value.toString()
                        val consumersecret = snapshot.child("consumersecret").value.toString()

                        daraja = Daraja.with(
                            consumerkey,
                            consumersecret,
                            object : DarajaListener<AccessToken> {
                                override fun onResult(@NonNull accessToken: AccessToken) {
                                    if (theactivity != null) {
                                        theactivity.makeLongToast("Gotten TokenP: ${accessToken.access_token}")
                                    }
                                }

                                override fun onError(error: String) {
                                    if (theactivity != null) {
                                        theactivity.makeLongToast(error)
                                    }
                                }
                            })
                    }
                } else {
                    theactivity.makeLongToast("Unable to set up Mpesa")
                }
            }
        })
    }

    private fun start_Listener_For_Payment(theactivity: FragmentActivity,source: View? ) {
        if (valueListener == null) {
            val reference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid).child("betPay")
            valueListener = reference.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (thedatabaseReference != null) {
                            if (snapshot.value.toString().equals("0")) {
                                thedatabaseReference!!.setValue(hashMap_Selected_Bet_By_Better).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        FirebaseDatabase.getInstance().getReference().child("bets").child(
                                            firebaseAuth.currentUser.uid).push().setValue(
                                            hashMap_Selected_Bet_By_Better).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                                theactivity.showAlertDialog("Your bet was placed")
                                                chosen_Answer = ""
                                                sendNotification(selected_id, "Joined Bet:     Name: ${thebettername},        Amount: Kes ${thebettamount}")

                                                hashMap_Selected_Bet_By_Better.clear()
                                                place_your_bet_open_edt?.setText("")

                                                val reference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid).child("betPay")
                                                valueListener?.let { it1 -> reference.removeEventListener(it1) }
                                                reference.removeValue()

                                                if (selected_id.equals(firebaseAuth.currentUser.uid.toString())) {
                                                    FirebaseDatabase.getInstance().reference.child("streams").child(selected_id).child("contribution").setValue(thebettamount)
                                                }

                                            } else {
                                                theactivity.makeLongToast("An error occured")
                                            }
                                        }
                                    } else {
                                        theactivity.showAlertDialog("Failed to place bet, ${it.exception.toString()}")
                                        hashMap_Selected_Bet_By_Better.clear()
                                        betamount?.setText("")
                                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                                    }
                                }
                            }else{
                                theactivity.makeLongToast("Your bet was not placed.")
                            }
                        }
                    }
                }
            })
        }
    }
}

