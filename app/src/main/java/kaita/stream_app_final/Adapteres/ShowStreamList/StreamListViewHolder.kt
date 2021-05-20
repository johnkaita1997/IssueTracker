package kaita.stream_app_final.Adapteres.ShowStreamList

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kaita.stream_app_final.Activities.ProfileOperations.YourStreams
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.R

class StreamListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var title: TextView = itemView.findViewById(R.id.l_title)
    private var id: TextView = itemView.findViewById(R.id.l_id)
    private var view_Stream: Button = itemView.findViewById(R.id.l_view_Stream)

    fun bind(how: StreamList, viewHolder: StreamListViewHolder, theactivity: FragmentActivity) {
        title.text = how.title
        id.text = "Stream Id: ${how.stamp}"
        view_Stream.setSafeOnClickListener {
            val intent = Intent(theactivity, YourStreams::class.java)
            intent.putExtra("stamp", how.stamp.toString())
            theactivity.startActivity(intent)
        }
    }
}
