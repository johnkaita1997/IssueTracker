package kaita.stream_app_final.Adapteres.Finish

import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kaita.stream_app_final.R

class FinishedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var bettername: TextView = itemView.findViewById(R.id.finish_bettername)
    private var bettermobile: TextView = itemView.findViewById(R.id.finish_bettermobile)
    private var betteremail: TextView = itemView.findViewById(R.id.finish_betteremail)

    private var betterchoice: TextView = itemView.findViewById(R.id.finish_betterchoice)
    private var betteramount: TextView = itemView.findViewById(R.id.finish_betteramount)
    private var wonorlost: TextView = itemView.findViewById(R.id.finish_wonorlost)

    fun bind(finished: Finished, viewHolder: FinishedViewHolder, theactivity: FragmentActivity) {
        bettername.text = finished.bettername
        bettermobile.text = finished.bettermobile
        betteremail.text = finished.betteremail
        betteramount.text = finished.betteramount
        wonorlost.text = finished.wonorlost
        betterchoice.text = finished.betterchoice!!.capitalize()
    }
}
