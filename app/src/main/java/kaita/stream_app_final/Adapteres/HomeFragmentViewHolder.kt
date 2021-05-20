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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kaita.stream_app_final.Activities.BottomSheet.BottomSheetDialogContainer
import kaita.stream_app_final.Activities.Modals.Post
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

    fun bind(post: Post, viewHolder: HomeFragmentViewHolder, theactivity: FragmentActivity) {

        val databaseReference = FirebaseDatabase.getInstance().reference.child("streams").child(post.stamp.toString())

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
            databaseReference.child("remove").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    theactivity.makeLongToast(error.message)
                }
                override fun onDataChange(remove_Snapshot: DataSnapshot) {
                    if (remove_Snapshot.exists()) {
                        if (!remove_Snapshot.value!!.equals("remove")) {

                            val howtoshare = post.howtoshare

                            if (post.remove.equals("remove")) {
                                theactivity.showAlertDialog(message = "This Stream is pending, check other steams")
                            } else {
                                if (howtoshare.equals("Privately") or howtoshare.equals("Private")) {
                                    theactivity.showAlertDialog(("This Stream is Private, check Other Streams"))
                                } else {
                                    val selected_User = databaseReference.ref

                                    Log.d("Wolololo", "bind: $selected_User")
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

                        } else {
                            theactivity.makeLongToast("This stream is pending check other streams")
                        }
                    } else {
                        theactivity.makeLongToast("This Stream does not exist or could be depending")
                    }
                }
            })
        }
    }
}

