package kaita.stream_app_final.Adapteres

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import kaita.stream_app_final.R

public class CustomAdapter(context: Context, arrayListDetails: ArrayList<Model>) : BaseAdapter() {

    private val layoutInflater: LayoutInflater
    private val arrayListDetails: ArrayList<Model>

    init {
        this.layoutInflater = LayoutInflater.from(context)
        this.arrayListDetails = arrayListDetails
    }

    override fun getCount(): Int {
        return arrayListDetails.size
    }

    override fun getItem(position: Int): Any {
        return arrayListDetails.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val listRowHolder: ListRowHolder

        if (convertView == null) {
            view = this.layoutInflater.inflate(R.layout.adapter_layout, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        val context = view!!.context

        listRowHolder.rtitle.text = arrayListDetails.get(position).title
        listRowHolder.rcomment.text = arrayListDetails.get(position).comments
        listRowHolder.rcreated_at.text = arrayListDetails.get(position).created_at
        listRowHolder.rdevelopername.text = arrayListDetails.get(position).developer
        listRowHolder.rloadcomments.text = arrayListDetails.get(position).commentload
        listRowHolder.rstate.text = arrayListDetails.get(position).state

        listRowHolder.rcomment.setSafeOnClickListener {
            val intent = Intent(context, Comments::class.java)
            intent.putExtra("index", position.toString())
            //context.startActivity(intent)
        }

        return view
    }
}

private class ListRowHolder(row: View?) {
    public val rtitle: TextView
    public val rcomment: TextView
    public val rcreated_at: TextView
    public val rdevelopername: TextView
    public val rloadcomments: TextView
    public val rstate: TextView
    public val linearLayout: LinearLayout

    init {
        this.rtitle = row!!.findViewById<TextView>(R.id.rtitle) as TextView
        this.rcomment = row.findViewById<TextView>(R.id.rcomment) as TextView
        this.rcreated_at = row.findViewById<TextView>(R.id.rcreated_at) as TextView
        this.rdevelopername = row.findViewById<TextView>(R.id.rdevelopername) as TextView
        this.rloadcomments = row.findViewById<TextView>(R.id.rloadcomments) as TextView
        this.rstate = row.findViewById<TextView>(R.id.rstate) as TextView
        this.linearLayout = row.findViewById<LinearLayout>(R.id.linearLayout) as LinearLayout
    }

}