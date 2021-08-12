package kaita.stream_app_final.Adapteres.EndBet

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.sendNotification
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.progressDialog
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import java.util.*


class EndBet_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val people_List = mutableListOf<String>()

    private var identity: TextView = itemView.findViewById(R.id.identity)
    private var clear_up_button: Button = itemView.findViewById(R.id.clear_Up)

    fun bind(post: EndBet, databaseReference: DatabaseReference, viewHolder: EndBet_ViewHolder, theactivity: FragmentActivity) {

        val position = adapterPosition
        identity.text = "Due: ${post.manage}"
        progressDialog = SpotsDialog.Builder().setContext(theactivity).build() as SpotsDialog

        clear_up_button.setSafeOnClickListener {

            if (!progressDialog.isShowing) { progressDialog.show()}

            val selected_User = databaseReference.ref
            val key = selected_User.key
            val theselected_id = key.toString()

            FirebaseDatabase.getInstance().getReference().child("streams").child(theselected_id).child("answer").addListenerForSingleValueEvent(
                    object :
                            ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            theactivity.makeLongToast(error.message)
                            if (progressDialog.isShowing) {
                                progressDialog.dismiss()
                            }
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                if (progressDialog.isShowing) {
                                    progressDialog.dismiss()
                                }
                                theactivity.showAlertDialog("Streamer has not closed the stream yet or Stream does not exist")
                            } else {
                                val correct_anser = snapshot.value.toString()
                                FirebaseDatabase.getInstance().getReference().child("streams")
                                    .child(theselected_id).child("stamp")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {
                                            if (progressDialog.isShowing) {
                                                progressDialog.dismiss()
                                            }
                                            theactivity.makeLongToast(error.message)
                                        }

                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val stampOne = snapshot.value.toString()
                                            Log.d("Mr Kaita", "onDataChange: Found stamp one $stampOne")


                                            delete_if_User_Has_Not_Betted_On_It(theactivity, theselected_id)

                                            FirebaseDatabase.getInstance().getReference().child("bets")
                                                .addListenerForSingleValueEvent(
                                                        object :
                                                                ValueEventListener {
                                                            override fun onCancelled(error: DatabaseError) {
                                                                if (progressDialog.isShowing) {
                                                                    progressDialog.dismiss()
                                                                }
                                                                theactivity.makeLongToast(error.message)
                                                            }

                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                FirebaseDatabase.getInstance().reference.child(
                                                                        "keys"
                                                                )
                                                                    .child(theselected_id)
                                                                    .addListenerForSingleValueEvent(
                                                                            object : ValueEventListener {
                                                                                override fun onCancelled(error: DatabaseError) {
                                                                                    if (progressDialog.isShowing) {
                                                                                        progressDialog.dismiss()
                                                                                    }
                                                                                    theactivity.makeLongToast("Error: ${error.message}")
                                                                                }

                                                                                override fun onDataChange(
                                                                                    theshot: DataSnapshot
                                                                                ) {
                                                                                    if (theshot.exists()) {
                                                                                        val actual_id =
                                                                                            theshot.value.toString()

                                                                                        for (value in snapshot.children) {
                                                                                            val key = value.key
                                                                                            Log.d(
                                                                                                    "Mr Kaita",
                                                                                                    "onDataChange: Found key one $key"
                                                                                            )

                                                                                            FirebaseDatabase.getInstance()
                                                                                                .getReference()
                                                                                                .child("bets")
                                                                                                .child(key.toString())
                                                                                                .addListenerForSingleValueEvent(
                                                                                                        object :
                                                                                                                ValueEventListener {
                                                                                                            override fun onCancelled(
                                                                                                                error: DatabaseError
                                                                                                            ) {
                                                                                                                if (progressDialog.isShowing) {
                                                                                                                    progressDialog.dismiss()
                                                                                                                }
                                                                                                                theactivity.makeLongToast(
                                                                                                                        error.message
                                                                                                                )
                                                                                                            }

                                                                                                            override fun onDataChange(
                                                                                                                snapshot: DataSnapshot
                                                                                                            ) {

                                                                                                                if (snapshot.exists()) {

                                                                                                                    if (snapshot.exists()) {
                                                                                                                    }

                                                                                                                    for (value in snapshot.children) {

                                                                                                                        val new_Key =
                                                                                                                            value.key
                                                                                                                        val tittle =
                                                                                                                            value.child(
                                                                                                                                    "betttittle"
                                                                                                                            ).value.toString()
                                                                                                                        val hechose =
                                                                                                                            value.child(
                                                                                                                                    "streamoption"
                                                                                                                            ).value.toString()

                                                                                                                        Log.d(
                                                                                                                                "Mr Kaita",
                                                                                                                                "onDataChange: Found new key $new_Key"
                                                                                                                        )

                                                                                                                        FirebaseDatabase.getInstance()
                                                                                                                            .getReference()
                                                                                                                            .child(
                                                                                                                                    "bets"
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    actual_id
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    new_Key.toString()
                                                                                                                            )
                                                                                                                            .child(
                                                                                                                                    "stamp"
                                                                                                                            )
                                                                                                                            .addListenerForSingleValueEvent(
                                                                                                                                    object :
                                                                                                                                            ValueEventListener {
                                                                                                                                        override fun onCancelled(
                                                                                                                                            error: DatabaseError
                                                                                                                                        ) {
                                                                                                                                            if (progressDialog.isShowing) {
                                                                                                                                                progressDialog.dismiss()
                                                                                                                                            }
                                                                                                                                            theactivity.makeLongToast(
                                                                                                                                                    error.message
                                                                                                                                            )
                                                                                                                                        }

                                                                                                                                        override fun onDataChange(
                                                                                                                                            snapshot: DataSnapshot
                                                                                                                                        ) {
                                                                                                                                            //Get the name and amount of the group
                                                                                                                                            FirebaseDatabase.getInstance()
                                                                                                                                                .getReference()
                                                                                                                                                .child(
                                                                                                                                                        "bets"
                                                                                                                                                )
                                                                                                                                                .child(
                                                                                                                                                        key.toString()
                                                                                                                                                )
                                                                                                                                                .child(
                                                                                                                                                        new_Key.toString()
                                                                                                                                                )
                                                                                                                                                .addListenerForSingleValueEvent(
                                                                                                                                                        object :
                                                                                                                                                                ValueEventListener {
                                                                                                                                                            override fun onCancelled(
                                                                                                                                                                error: DatabaseError
                                                                                                                                                            ) {
                                                                                                                                                                if (progressDialog.isShowing) {
                                                                                                                                                                    progressDialog.dismiss()
                                                                                                                                                                }
                                                                                                                                                                theactivity.makeLongToast(
                                                                                                                                                                        error.message
                                                                                                                                                                )
                                                                                                                                                            }

                                                                                                                                                            override fun onDataChange(
                                                                                                                                                                levelone_snapshot: DataSnapshot
                                                                                                                                                            ) {
                                                                                                                                                                if (levelone_snapshot.exists()) {

                                                                                                                                                                    val stampTwo =
                                                                                                                                                                        snapshot.value.toString()
                                                                                                                                                                    val bettertype =
                                                                                                                                                                        "Regular"
                                                                                                                                                                    val end_name =
                                                                                                                                                                        levelone_snapshot.child(
                                                                                                                                                                                "bettername"
                                                                                                                                                                        ).value.toString()
                                                                                                                                                                    val end_mobile =
                                                                                                                                                                        levelone_snapshot.child(
                                                                                                                                                                                "bettermobile"
                                                                                                                                                                        ).value.toString()
                                                                                                                                                                    val end_email =
                                                                                                                                                                        levelone_snapshot.child(
                                                                                                                                                                                "betteremail"
                                                                                                                                                                        ).value.toString()
                                                                                                                                                                    val end_bettamount =
                                                                                                                                                                        levelone_snapshot.child(
                                                                                                                                                                                "bettamount"
                                                                                                                                                                        ).value.toString()
                                                                                                                                                                    val end_bettchoice =
                                                                                                                                                                        hechose

                                                                                                                                                                    //Get the host betting also
                                                                                                                                                                    FirebaseDatabase.getInstance()
                                                                                                                                                                        .getReference()
                                                                                                                                                                        .child(
                                                                                                                                                                                "streams"
                                                                                                                                                                        )
                                                                                                                                                                        .child(
                                                                                                                                                                                theselected_id
                                                                                                                                                                        )
                                                                                                                                                                        .addListenerForSingleValueEvent(
                                                                                                                                                                                object :
                                                                                                                                                                                        ValueEventListener {
                                                                                                                                                                                    override fun onCancelled(
                                                                                                                                                                                        error: DatabaseError
                                                                                                                                                                                    ) {
                                                                                                                                                                                        if (progressDialog.isShowing) {
                                                                                                                                                                                            progressDialog.dismiss()
                                                                                                                                                                                        }
                                                                                                                                                                                        theactivity.makeLongToast(
                                                                                                                                                                                                error.message.toString()
                                                                                                                                                                                        )
                                                                                                                                                                                    }

                                                                                                                                                                                    override fun onDataChange(
                                                                                                                                                                                        snapshot: DataSnapshot
                                                                                                                                                                                    ) {
                                                                                                                                                                                        if (snapshot.exists()) {
                                                                                                                                                                                            val host_end_name =
                                                                                                                                                                                                snapshot.child(
                                                                                                                                                                                                        "hostname"
                                                                                                                                                                                                ).value.toString()
                                                                                                                                                                                            val hostid =
                                                                                                                                                                                                snapshot.child(
                                                                                                                                                                                                        "host"
                                                                                                                                                                                                ).value.toString()

                                                                                                                                                                                            FirebaseDatabase.getInstance()
                                                                                                                                                                                                .getReference()
                                                                                                                                                                                                .child(
                                                                                                                                                                                                        "users"
                                                                                                                                                                                                )
                                                                                                                                                                                                .child(
                                                                                                                                                                                                        hostid
                                                                                                                                                                                                )
                                                                                                                                                                                                .addListenerForSingleValueEvent(
                                                                                                                                                                                                        object :
                                                                                                                                                                                                                ValueEventListener {
                                                                                                                                                                                                            override fun onCancelled(
                                                                                                                                                                                                                error: DatabaseError
                                                                                                                                                                                                            ) {
                                                                                                                                                                                                                if (progressDialog.isShowing) {
                                                                                                                                                                                                                    progressDialog.dismiss()
                                                                                                                                                                                                                }
                                                                                                                                                                                                                theactivity.makeLongToast(
                                                                                                                                                                                                                        error.message
                                                                                                                                                                                                                )
                                                                                                                                                                                                            }

                                                                                                                                                                                                            override fun onDataChange(
                                                                                                                                                                                                                snapshot: DataSnapshot
                                                                                                                                                                                                            ) {
                                                                                                                                                                                                                if (snapshot.exists()) {

                                                                                                                                                                                                                    val host_end_mobile =
                                                                                                                                                                                                                        snapshot.child(
                                                                                                                                                                                                                                "mobileNumber"
                                                                                                                                                                                                                        ).value.toString()
                                                                                                                                                                                                                    val host_end_email =
                                                                                                                                                                                                                        snapshot.child(
                                                                                                                                                                                                                                "email"
                                                                                                                                                                                                                        ).value.toString()

                                                                                                                                                                                                                    val second_addition =
                                                                                                                                                                                                                        "Name: $host_end_name\nMobile: $host_end_mobile\nEmail: $host_end_email\nRole: Host"
                                                                                                                                                                                                                    val addition =
                                                                                                                                                                                                                        "Name: $end_name\nMobile: $end_mobile\nEmail: $end_email\nAmount: $end_bettamount\nChoice: $end_bettchoice\nRole: Regular"
                                                                                                                                                                                                                    val combined =
                                                                                                                                                                                                                        "$second_addition\n$addition"

                                                                                                                                                                                                                    Log.d(
                                                                                                                                                                                                                            "MAMBO",
                                                                                                                                                                                                                            "KUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU: $combined"
                                                                                                                                                                                                                    )

                                                                                                                                                                                                                    Log.d(
                                                                                                                                                                                                                            "Mr Kaita",
                                                                                                                                                                                                                            "onDataChange: Found stamp two $stampTwo"
                                                                                                                                                                                                                    )

                                                                                                                                                                                                                    if (stampOne.equals(
                                                                                                                                                                                                                                stampTwo
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                        FirebaseDatabase.getInstance()
                                                                                                                                                                                                                            .getReference()
                                                                                                                                                                                                                            .child(
                                                                                                                                                                                                                                    "bets"
                                                                                                                                                                                                                            )
                                                                                                                                                                                                                            .child(
                                                                                                                                                                                                                                    actual_id
                                                                                                                                                                                                                            )
                                                                                                                                                                                                                            .child(
                                                                                                                                                                                                                                    new_Key.toString()
                                                                                                                                                                                                                            )
                                                                                                                                                                                                                            .child(
                                                                                                                                                                                                                                    "bettstate"
                                                                                                                                                                                                                            )
                                                                                                                                                                                                                            .setValue(
                                                                                                                                                                                                                                    "Closed"
                                                                                                                                                                                                                            )
                                                                                                                                                                                                                            .addOnCompleteListener {
                                                                                                                                                                                                                                if (it.isComplete) {
                                                                                                                                                                                                                                    var win_or_lose =
                                                                                                                                                                                                                                        ""
                                                                                                                                                                                                                                    var right_answer =
                                                                                                                                                                                                                                        correct_anser
                                                                                                                                                                                                                                    if (!hechose.equals(
                                                                                                                                                                                                                                                correct_anser
                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                                        sendNotification(
                                                                                                                                                                                                                                                theselected_id,
                                                                                                                                                                                                                                                "You lost:       Bet: $tittle"
                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                        win_or_lose =
                                                                                                                                                                                                                                            "Lost"

                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                        sendNotification(
                                                                                                                                                                                                                                                theselected_id,
                                                                                                                                                                                                                                                "You Won:      Bet: $tittle,    Stream will contact you for payment."
                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                        win_or_lose =
                                                                                                                                                                                                                                            "Won"
                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                    val tagMap =
                                                                                                                                                                                                                                        HashMap<String, String>()
                                                                                                                                                                                                                                    tagMap["tag"] =
                                                                                                                                                                                                                                        theselected_id

                                                                                                                                                                                                                                    val moneyMap =
                                                                                                                                                                                                                                        HashMap<String, Any>()
                                                                                                                                                                                                                                    moneyMap["hostname"] =
                                                                                                                                                                                                                                        host_end_name
                                                                                                                                                                                                                                    moneyMap["hostmobile"] =
                                                                                                                                                                                                                                        host_end_mobile
                                                                                                                                                                                                                                    moneyMap["hostemail"] =
                                                                                                                                                                                                                                        host_end_email
                                                                                                                                                                                                                                    moneyMap["role"] =
                                                                                                                                                                                                                                        "Host"
                                                                                                                                                                                                                                    moneyMap["bettername"] =
                                                                                                                                                                                                                                        end_name
                                                                                                                                                                                                                                    moneyMap["bettermobile"] =
                                                                                                                                                                                                                                        end_mobile
                                                                                                                                                                                                                                    moneyMap["betteremail"] =
                                                                                                                                                                                                                                        end_email
                                                                                                                                                                                                                                    moneyMap["betteramount"] =
                                                                                                                                                                                                                                        end_bettamount
                                                                                                                                                                                                                                    moneyMap["betterchoice"] =
                                                                                                                                                                                                                                        end_bettchoice
                                                                                                                                                                                                                                    moneyMap["betterrole"] =
                                                                                                                                                                                                                                        "Regular"
                                                                                                                                                                                                                                    moneyMap["title"] =
                                                                                                                                                                                                                                        tittle
                                                                                                                                                                                                                                    moneyMap["wonorlost"] =
                                                                                                                                                                                                                                        win_or_lose
                                                                                                                                                                                                                                    moneyMap["correctanswer"] =
                                                                                                                                                                                                                                        correct_anser

                                                                                                                                                                                                                                    FirebaseDatabase.getInstance().reference.child(
                                                                                                                                                                                                                                            "money"
                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                        .child(
                                                                                                                                                                                                                                                hostid
                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                        .push()
                                                                                                                                                                                                                                        .setValue(
                                                                                                                                                                                                                                                moneyMap
                                                                                                                                                                                                                                        )


                                                                                                                                                                                                                                    FirebaseDatabase.getInstance().reference.child(
                                                                                                                                                                                                                                            "keys"
                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                        .child(
                                                                                                                                                                                                                                                theselected_id
                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                        .addListenerForSingleValueEvent(
                                                                                                                                                                                                                                                object :
                                                                                                                                                                                                                                                        ValueEventListener {
                                                                                                                                                                                                                                                    override fun onCancelled(
                                                                                                                                                                                                                                                        error: DatabaseError
                                                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                                                        if (progressDialog.isShowing) {
                                                                                                                                                                                                                                                            progressDialog.dismiss()
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                        theactivity.makeLongToast(
                                                                                                                                                                                                                                                                "Error: ${error.message}"
                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                                    override fun onDataChange(
                                                                                                                                                                                                                                                        keysnapshot: DataSnapshot
                                                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                                                        if (keysnapshot.exists()) {
                                                                                                                                                                                                                                                            val theid =
                                                                                                                                                                                                                                                                keysnapshot.value.toString()

                                                                                                                                                                                                                                                            FirebaseDatabase.getInstance().reference.child(
                                                                                                                                                                                                                                                                    "ids"
                                                                                                                                                                                                                                                            )
                                                                                                                                                                                                                                                                .child(
                                                                                                                                                                                                                                                                        theid
                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                .child(
                                                                                                                                                                                                                                                                        theselected_id
                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                                                .removeValue()
                                                                                                                                                                                                                                                                .addOnCompleteListener {
                                                                                                                                                                                                                                                                    if (it.isSuccessful) {

                                                                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                                                                        if (progressDialog.isShowing) {
                                                                                                                                                                                                                                                                            progressDialog.dismiss()
                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                        theactivity.makeLongToast(
                                                                                                                                                                                                                                                                                "Exception: ${it.exception.toString()}"
                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                }

                                                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                                                            if (progressDialog.isShowing) {
                                                                                                                                                                                                                                                                progressDialog.dismiss()
                                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                                            theactivity.makeLongToast(
                                                                                                                                                                                                                                                                    "Missing Id!"
                                                                                                                                                                                                                                                            )
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                })

                                                                                                                                                                                                                                    FirebaseChecker().homeRef_Streams.child(
                                                                                                                                                                                                                                            theselected_id
                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                        .removeValue()
                                                                                                                                                                                                                                        .addOnCompleteListener {
                                                                                                                                                                                                                                            if (it.isSuccessful) {
                                                                                                                                                                                                                                                FirebaseDatabase.getInstance()
                                                                                                                                                                                                                                                    .getReference()
                                                                                                                                                                                                                                                    .child(
                                                                                                                                                                                                                                                            "manage"
                                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                                    .child(
                                                                                                                                                                                                                                                            theselected_id
                                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                                    .removeValue()
                                                                                                                                                                                                                                                    .addOnCompleteListener {
                                                                                                                                                                                                                                                        if (it.isSuccessful) {
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                            } else {
                                                                                                                                                                                                                                                if (progressDialog.isShowing) {
                                                                                                                                                                                                                                                    progressDialog.dismiss()
                                                                                                                                                                                                                                                }
                                                                                                                                                                                                                                                theactivity.showAlertDialog(
                                                                                                                                                                                                                                                        "Error Occured: ${it.exception.toString()}"
                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                                            }
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    //Make Payment Email

                                                                                                                                                                                                                                } else {
                                                                                                                                                                                                                                    if (progressDialog.isShowing) {
                                                                                                                                                                                                                                        progressDialog.dismiss()
                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                    theactivity.showAlertDialog(
                                                                                                                                                                                                                                            "Error: ${it.exception.toString()}"
                                                                                                                                                                                                                                    )
                                                                                                                                                                                                                                }
                                                                                                                                                                                                                            }
                                                                                                                                                                                                                    } else {
                                                                                                                                                                                                                        Log.d(
                                                                                                                                                                                                                                "Walai",
                                                                                                                                                                                                                                "Not Equal  StampOne is $stampOne and Stamp Two is $stampTwo"
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                    }

                                                                                                                                                                                                                }
                                                                                                                                                                                                            }
                                                                                                                                                                                                        })
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                })
                                                                                                                                                                } else {
                                                                                                                                                                    if (progressDialog.isShowing) {
                                                                                                                                                                        progressDialog.dismiss()
                                                                                                                                                                    }
                                                                                                                                                                    theactivity.makeLongToast(
                                                                                                                                                                            "Data error occured"
                                                                                                                                                                    )
                                                                                                                                                                }
                                                                                                                                                            }
                                                                                                                                                        })
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                            )
                                                                                                                    }

                                                                                                                } else {

                                                                                                                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                                                                                    //              CHECK HERE
                                                                                                                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


                                                                                                                }

                                                                                                            }
                                                                                                        })
                                                                                        }

                                                                                        val collectionref: CollectionReference =
                                                                                            Constants.database.collection(
                                                                                                    "streams"
                                                                                            )
                                                                                                .document(
                                                                                                        theselected_id
                                                                                                )
                                                                                                .collection("options")
                                                                                        collectionref.get()
                                                                                            .addOnCompleteListener {
                                                                                                if (it.isSuccessful) {
                                                                                                    for (document in it.result!!) {
                                                                                                        val thedocumentid =
                                                                                                            document.id
                                                                                                        val ref: DocumentReference =
                                                                                                            Constants.database.collection(
                                                                                                                    "streams"
                                                                                                            )
                                                                                                                .document(
                                                                                                                        theselected_id
                                                                                                                )
                                                                                                                .collection(
                                                                                                                        "options"
                                                                                                                )
                                                                                                                .document(
                                                                                                                        thedocumentid
                                                                                                                )
                                                                                                        ref.delete()
                                                                                                    }

                                                                                                    val ref: DocumentReference =
                                                                                                        Constants.database.collection(
                                                                                                                "streams"
                                                                                                        )
                                                                                                            .document(
                                                                                                                    theselected_id
                                                                                                            )
                                                                                                    ref.delete()
                                                                                                        .addOnCompleteListener {
                                                                                                            if (it.isSuccessful) {
                                                                                                                send_Email(
                                                                                                                        people_List,
                                                                                                                        theactivity
                                                                                                                )

                                                                                                                FirebaseDatabase.getInstance().reference.child("removetags").push().setValue(theselected_id).addOnCompleteListener {
                                                                                                                    if (it.isSuccessful) {
                                                                                                                    } else {
                                                                                                                        theactivity.makeLongToast(it.exception.toString())
                                                                                                                    }
                                                                                                                }

                                                                                                            } else {
                                                                                                                if (progressDialog.isShowing) {
                                                                                                                    progressDialog.dismiss()
                                                                                                                }
                                                                                                                theactivity.makeLongToast(
                                                                                                                        "Error: ${it.exception.toString()}"
                                                                                                                )
                                                                                                            }
                                                                                                        }

                                                                                                } else {
                                                                                                    if (progressDialog.isShowing) {
                                                                                                        progressDialog.dismiss()
                                                                                                    }
                                                                                                    theactivity.makeLongToast(
                                                                                                            it.exception.toString()
                                                                                                    )
                                                                                                }
                                                                                            }

                                                                                    } else {
                                                                                        if (progressDialog.isShowing) {
                                                                                            progressDialog.dismiss()
                                                                                        }
                                                                                        theactivity.makeLongToast(
                                                                                                "Error!, missing info."
                                                                                        )
                                                                                    }
                                                                                }
                                                                            })
                                                            }
                                                        })
                                        }
                                    })
                            }
                        }
                    })
        }
    }

    private fun delete_if_User_Has_Not_Betted_On_It(theactivity: FragmentActivity, theselected_id: String) {
        FirebaseDatabase.getInstance().reference.child("keys").child(theselected_id).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                theactivity.makeLongToast("Error: ${error.message}")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    FirebaseDatabase.getInstance().reference.child("streams").child(theselected_id).child("bets").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            theactivity.makeLongToast("Error: ${error.message}")
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                //              COMMENT SNAPSHOT DOES NOT EXIST NO BETS HAVE BEEN PLACED ON IT
                                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                val collectionref: CollectionReference =
                                    Constants.database.collection("streams").document(theselected_id)
                                        .collection("options")
                                collectionref.get()
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            for (document in it.result!!) {
                                                val thedocumentid =
                                                    document.id
                                                val ref: DocumentReference =
                                                    Constants.database.collection(
                                                            "streams"
                                                    )
                                                        .document(
                                                                theselected_id
                                                        )
                                                        .collection(
                                                                "options"
                                                        )
                                                        .document(
                                                                thedocumentid
                                                        )
                                                ref.delete()
                                            }

                                            val ref: DocumentReference =
                                                Constants.database.collection(
                                                        "streams"
                                                )
                                                    .document(
                                                            theselected_id
                                                    )
                                            ref.delete()
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {

                                                    } else {
                                                        if (progressDialog.isShowing) {
                                                            progressDialog.dismiss()
                                                        }
                                                        theactivity.makeLongToast(
                                                                "Error: ${it.exception.toString()}"
                                                        )
                                                    }
                                                }

                                        } else {
                                            if (progressDialog.isShowing) {
                                                progressDialog.dismiss()
                                            }
                                            theactivity.makeLongToast(
                                                    it.exception.toString()
                                            )
                                        }
                                    }

                                val ref: DocumentReference =
                                    Constants.database.collection(
                                            "streams"
                                    )
                                        .document(
                                                theselected_id
                                        )
                                ref.delete()

                                FirebaseDatabase.getInstance().reference.child("manage").child(theselected_id).removeValue()
                                FirebaseDatabase.getInstance().reference.child("streams").child(theselected_id).removeValue()
                                FirebaseDatabase.getInstance().reference.child("keys").child(theselected_id).addListenerForSingleValueEvent(object: ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        theactivity.makeLongToast("Error: ${error.message}")
                                    }
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            val theid  = snapshot.value.toString()
                                            FirebaseDatabase.getInstance().reference.child("ids").child(theid).child(theselected_id).removeValue()
                                            theactivity.makeLongToast("Operation was successful")
                                        } else {
                                            //theactivity.makeLongToast("Error!, missing info.")
                                        }
                                    }
                                })
                            } else {
                               // theactivity.makeLongToast("Error!, missing info.")
                            }
                        }
                    })

                } else {
                    //theactivity.makeLongToast("Error!, missing info.")
                }
            }
        })



    }

    private fun send_Email(peopleList: MutableList<String>, theactivity: FragmentActivity) {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        theactivity.showAlertDialog("Operation was completed")
    }
}
