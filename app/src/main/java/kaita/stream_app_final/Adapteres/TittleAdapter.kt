/*
package kaita.stream_app_final.Adapteres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kaita.stream_app_final.R

class TittleAdapter(private val titlelist: ArrayList<Tittle>) : RecyclerView.Adapter<TittleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txvDestination.text = titlelist[position].title
        holder.itemView.setOnClickListener { v ->
        }
    }

    override fun getItemCount(): Int {
        return titlelist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txvDestination: TextView = itemView.findViewById(R.id.txv_destination)

        override fun toString(): String {
            return """${super.toString()} '${txvDestination.text}'"""
        }
    }
}*/
