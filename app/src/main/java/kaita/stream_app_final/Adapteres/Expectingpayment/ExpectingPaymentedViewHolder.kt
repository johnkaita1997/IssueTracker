package kaita.stream_app_final.Adapteres.Expectingpayment

import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import kaita.stream_app_final.R

class ExpectingPaymentedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var stamp: TextView = itemView.findViewById(R.id.e_stamp)
    private var betttittle: TextView = itemView.findViewById(R.id.e_betttittle)
    private var bettername: TextView = itemView.findViewById(R.id.e_better_Name)
    private var bettermobile: TextView = itemView.findViewById(R.id.e_bettermobile)
    private var betteridnumber: TextView = itemView.findViewById(R.id.e_betteridnumber)
    private var betterid: TextView = itemView.findViewById(R.id.e_betterid)
    private var betteremail: TextView = itemView.findViewById(R.id.e_betteremail)
    private var bettdate: TextView = itemView.findViewById(R.id.e_bettdate)
    private var bettamount: TextView = itemView.findViewById(R.id.e_bettamount)
    private var streamoption: TextView = itemView.findViewById(R.id.e_streamoption)

    fun bind(expectingpaymented: ExpectingPaymented, viewHolder: ExpectingPaymentedViewHolder, theactivity: FragmentActivity, databaseRerence: DatabaseReference) {

        stamp.text = "${expectingpaymented.stamp}"
        betttittle.text = "${expectingpaymented.betttittle}"
        bettername.text = "${expectingpaymented.bettername}"
        bettermobile.text = "${expectingpaymented.bettermobile}"
        betteridnumber.text = "${expectingpaymented.betteridnumber}"
        bettername.text = "${expectingpaymented.bettername}"
        betterid.text = "${expectingpaymented.betterid}"
        betteremail.text = "${expectingpaymented.betteremail}"
        bettdate.text = "${expectingpaymented.bettdate}"
        bettamount.text = "${expectingpaymented.bettamount}"
        streamoption.text = "${expectingpaymented.streamoption}"
    }
}
