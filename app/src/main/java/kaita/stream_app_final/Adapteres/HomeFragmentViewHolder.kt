package kaita.stream_app_final.Adapteres

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kaita.stream_app_final.Activities.BottomSheet.BottomSheetDialogContainer
import kaita.stream_app_final.Activities.Modals.EndBet
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.loaded
import kaita.stream_app_final.AppConstants.Constants.selected_id
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class HomeFragmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var lastDay: TextView = itemView.findViewById(R.id.f_due)
    private var betTittle: TextView = itemView.findViewById(R.id.f_title)
    private var name: TextView = itemView.findViewById(R.id.f_Name)
    private var hostimaged: ImageView = itemView.findViewById(R.id.f_Image) as CircleImageView
    private var discover_Button: Button = itemView.findViewById(R.id.f_discover)
    private var cash: TextView = itemView.findViewById(R.id.f_cash)

    fun bind(post: Post, databaseReference: DatabaseReference, viewHolder: HomeFragmentViewHolder, theactivity: FragmentActivity) {

        databaseReference.child("bets").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                theactivity.makeLongToast(error.message)
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var number_Of_Bets = snapshot.childrenCount.toString()
                    cash.text = "$number_Of_Bets Bets"
                } else {
                    cash.text = "0 Bets"
                }
            }
        })

        betTittle.text = "${post.title}"
        name.text = "${post.hostname}"

        //Get date and time from lastday
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val final_current_day: Date = formatter.parse(post.lastday)

        lastDay.text = "Due: ${final_current_day}"

        Picasso
            .get()
            .load(post.hostimage)
            .placeholder(R.drawable.personb)
            .noFade()
            .into(hostimaged)

        discover_Button.setSafeOnClickListener {

            val howtoshare = post.howtoshare

            if (post.remove.equals("remove")) {
                theactivity.showAlertDialog(message = "This Stream is pending, check other steams")
            } else {
                if (howtoshare.equals("Private")) {
                    theactivity.showAlertDialog(("This Stream is Private, check Other Streams"))
                } else {
                    val selected_User = databaseReference.ref
                    val key = selected_User.key
                    selected_id = key.toString()

                    val bottomSheet = BottomSheetDialogContainer()
                    val metrics = DisplayMetrics()

                    val bundle = Bundle()
                    bundle.putString("key", selected_id)
                    bottomSheet.arguments = bundle

                    CoroutineScope(Dispatchers.IO).launch {
                        theactivity.windowManager?.defaultDisplay?.getMetrics(metrics)
                        if (loaded == false) {
                            bottomSheet.show(theactivity.supportFragmentManager, key)
                            loaded = true
                        }
                    }
                }
            }
        }
    }
}


class EndBet_ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val people_List = mutableListOf<String>()

    private var identity: TextView = itemView.findViewById(R.id.identity)
    private var clear_up_button: Button = itemView.findViewById(R.id.clear_Up)

    fun bind(post: EndBet, databaseReference: DatabaseReference, viewHolder: EndBet_ViewHolder, theactivity: FragmentActivity) {
        val position = adapterPosition
        identity.text = "Due: ${post.manage}"

        clear_up_button.setSafeOnClickListener {

            val selected_User = databaseReference.ref
            val key = selected_User.key
            selected_id = key.toString()

            FirebaseDatabase.getInstance().getReference().child("streams").child(selected_id).child("answer").addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    theactivity.makeLongToast(error.message)
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        theactivity.showAlertDialog("Streamer has not closed the stream yet or Stream does not exist")
                    } else {
                        val correct_anser = snapshot.value.toString()
                        FirebaseDatabase.getInstance().getReference().child("streams").child(selected_id).child("stamp")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {
                                    theactivity.makeLongToast(error.message)
                                }
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val stampOne = snapshot.value.toString()
                                    Log.d("Mr Kaita", "onDataChange: Found stamp one $stampOne")

                                    FirebaseDatabase.getInstance().getReference().child("bets").addListenerForSingleValueEvent(object:
                                        ValueEventListener {
                                        override fun onCancelled(error: DatabaseError) {
                                            theactivity.makeLongToast(error.message)
                                        }
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (value in snapshot.children) {
                                                //Get the key
                                                val key = value.key
                                                Log.d("Mr Kaita", "onDataChange: Found key one $key")
                                                FirebaseDatabase.getInstance().getReference().child("bets").child(key.toString()).addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onCancelled(error: DatabaseError) {
                                                        theactivity.makeLongToast(error.message)
                                                    }
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        for (value in snapshot.children) {

                                                            val new_Key = value.key

                                                            val tittle = value.child("betttittle").value.toString()
                                                            val hechose = value.child("streamoption").value.toString()

                                                            Log.d("Mr Kaita", "onDataChange: Found new key $new_Key")

                                                            FirebaseDatabase.getInstance().getReference().child("bets").child(key.toString()).child(new_Key.toString()).child("stamp").addListenerForSingleValueEvent(
                                                                object : ValueEventListener {
                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        theactivity.makeLongToast(error.message)
                                                                    }
                                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                                        //Get the name and amount of the group
                                                                        FirebaseDatabase.getInstance().getReference().child("bets").child(key.toString()).child(new_Key.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
                                                                            override fun onCancelled(error: DatabaseError) {
                                                                                theactivity.makeLongToast(error.message)
                                                                            }
                                                                            override fun onDataChange(levelone_snapshot: DataSnapshot) {
                                                                                if (levelone_snapshot.exists()) {

                                                                                    val stampTwo = snapshot.value.toString()

                                                                                    val bettertype = "Regular"
                                                                                    val end_name = levelone_snapshot.child("bettername").value.toString()
                                                                                    val end_mobile = levelone_snapshot.child("bettermobile").value.toString()
                                                                                    val end_email = levelone_snapshot.child("betteremail").value.toString()
                                                                                    val end_bettamount = levelone_snapshot.child("bettamount").value.toString()
                                                                                    val end_bettchoice = hechose

                                                                                    //Get the host betting also
                                                                                    FirebaseDatabase.getInstance().getReference().child("streams").child( selected_id).addListenerForSingleValueEvent(object : ValueEventListener{
                                                                                        override fun onCancelled(error: DatabaseError) {
                                                                                            theactivity.makeLongToast(error.message.toString())
                                                                                        }
                                                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                                                            if (snapshot.exists()) {
                                                                                                val host_end_name = snapshot.child("hostname").value.toString()
                                                                                                val hostid = snapshot.child("host").value.toString()

                                                                                                FirebaseDatabase.getInstance().getReference().child("users").child(hostid).addListenerForSingleValueEvent(object : ValueEventListener{
                                                                                                    override fun onCancelled(error: DatabaseError) {
                                                                                                        theactivity.makeLongToast(error.message)
                                                                                                    }
                                                                                                    override fun onDataChange(snapshot: DataSnapshot ) {
                                                                                                        if (snapshot.exists()) {

                                                                                                            val host_end_mobile = snapshot.child("mobileNumber").value.toString()
                                                                                                            val host_end_email = snapshot.child("email").value.toString()

                                                                                                            val second_addition = "Name: $host_end_name\nMobile: $host_end_mobile\nEmail: $host_end_email\nRole: Host"
                                                                                                            val addition = "Name: $end_name\nMobile: $end_mobile\nEmail: $end_email\nAmount: $end_bettamount\nChoice: $end_bettchoice\nRole: Regular"
                                                                                                            val combined = "$second_addition\n$addition"

                                                                                                            Log.d( "MAMBO","KUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU: $combined")

                                                                                                            people_List.add(combined)

                                                                                                            Log.d("Mr Kaita", "onDataChange: Found stamp two $stampTwo")

                                                                                                            if (stampOne.equals(stampTwo)) {
                                                                                                                FirebaseDatabase.getInstance()
                                                                                                                    .getReference().child("bets")
                                                                                                                    .child(key.toString())
                                                                                                                    .child(new_Key.toString())
                                                                                                                    .child("bettstate")
                                                                                                                    .setValue("Closed")
                                                                                                                    .addOnCompleteListener {
                                                                                                                        if (it.isComplete) {
                                                                                                                            Log.d("Mr Kaita", "Done")
                                                                                                                            if (!hechose.equals(correct_anser)) {
                                                                                                                                sendNotification(key.toString(), "You lost:       Bet: $tittle")
                                                                                                                            } else {
                                                                                                                                sendNotification(key.toString(), "You Won:      Bet: $tittle,    Stream will contact you for payment.")
                                                                                                                            }

                                                                                                                            FirebaseChecker().homeRef_Streams.child(
                                                                                                                                Constants.firebaseAuth.currentUser.uid).removeValue().addOnCompleteListener {
                                                                                                                                if (it.isSuccessful) {
                                                                                                                                    FirebaseDatabase.getInstance().getReference().child("manage").child(
                                                                                                                                        selected_id).removeValue().addOnCompleteListener {
                                                                                                                                        if (it.isSuccessful) {
                                                                                                                                            //source.admin_identity.setText("")
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                } else {
                                                                                                                                    theactivity.showAlertDialog("Error Occured: ${it.exception.toString()}")
                                                                                                                                }
                                                                                                                            }

                                                                                                                            //Make Payment Email

                                                                                                                        } else {
                                                                                                                            theactivity.showAlertDialog("Error: ${it.exception.toString()}"
                                                                                                                            )
                                                                                                                        }
                                                                                                                    }
                                                                                                            } else {
                                                                                                                Log.d("Walai", "Not Equal  StampOne is $stampOne and Stamp Two is $stampTwo")
                                                                                                            }

                                                                                                        }
                                                                                                    }

                                                                                                })

                                                                                            }
                                                                                        }
                                                                                    })
                                                                                } else {
                                                                                    theactivity.makeLongToast("Data error occured")
                                                                                }
                                                                            }
                                                                        })
                                                                    }
                                                                }
                                                            )
                                                        }
                                                        send_Email(people_List, theactivity)
                                                    }
                                                })
                                            }
                                        }
                                    })
                                }
                            })
                    }
                }
            })
        }
    }

    private fun send_Email(peopleList: MutableList<String>,theactivity: FragmentActivity) {
        var string = ""
        peopleList.forEachIndexed { index, value ->
            string = string + "\n" + index
        }
        FirebaseDatabase.getInstance().reference.child("money").push().setValue(string).addOnCompleteListener {
            if (it.isSuccessful) {
                theactivity.showAlertDialog("Task was successful")
            } else {
                theactivity.makeLongToast(it.exception.toString())
            }
        }
    }
}
