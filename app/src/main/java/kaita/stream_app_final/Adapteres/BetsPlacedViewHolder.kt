package kaita.stream_app_final.Adapteres

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kaita.stream_app_final.Activities.Modals.BetsPlaced
import kaita.stream_app_final.Activities.Modals.BetsPlaced_Final
import kaita.stream_app_final.Activities.ProfileOperations.ActivityForViewingAllTheBetters
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R

class BetsPlacedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var bettername: TextView = itemView.findViewById(R.id.better_name)
    private var betteramount: TextView = itemView.findViewById(R.id.better_amount)

    fun bind(betsplaced: BetsPlaced, databaseReference: DatabaseReference, viewHolder: BetsPlacedViewHolder, theactivity: FragmentActivity, source: View?) {
        bettername.text = betsplaced.bettername
        betteramount.text = "Kes: " + betsplaced.bettamount + "\nBet: ${betsplaced.streamoption!!.toUpperCase()}"
    }

}



class BetsPlacedViewHolder_Final(itemView: View) : RecyclerView.ViewHolder(itemView){

    private var tittle: TextView = itemView.findViewById(R.id.mambo_title)
    private var amount: TextView = itemView.findViewById(R.id.mambo_amount)
    private var dateplaced: TextView = itemView.findViewById(R.id.mambo_datePlaced )
    private var optionchose: TextView = itemView.findViewById(R.id.mambo_option_Chose)
    private var bettstate: TextView = itemView.findViewById(R.id.mambo_betstate)
    private var showOptions: TextView = itemView.findViewById(R.id.mambo_ShowBetters_Button)

    fun bind(betsplaced: BetsPlaced_Final, databaseReference: DatabaseReference, viewHolder: BetsPlacedViewHolder_Final, theactivity: FragmentActivity, source: View?) {

        tittle.text = betsplaced.betttittle
        amount.text = "Placed: Kes: ${betsplaced.bettamount}"
        dateplaced.text = betsplaced.bettdate
        bettstate.text = betsplaced.bettstate
        optionchose.text = betsplaced.streamoption!!.toUpperCase()

        showOptions.setSafeOnClickListener {
            val the_Id = betsplaced.streamid
                FirebaseDatabase.getInstance().getReference().child("bets").child(the_Id.toString()).addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            theactivity.showAlertDialog("It appears this Stream was closed")
                        } else {
                            val intent = Intent(theactivity, ActivityForViewingAllTheBetters::class.java)
                            intent.putExtra("access",  the_Id)
                            theactivity.startActivity(intent)
                        }
                    }

                })
        }

    }

}