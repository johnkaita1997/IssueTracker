package smartherd.kenyamessagesolution.Adapteres

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.onecardcontactlist.view.*


data class Player(val name: Double = 0.0, val date: Double = 0.0, val mobile: Double = 0.0) :
    java.io.Serializable

data class bring(val rowname: String, val rownumber: String, val numbersOrletters: String) :
    java.io.Serializable

data class Post(
    var name: String? = null,
    var mobile: Long? = null
)


class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var authorView: TextView = itemView.cardname
    private var messageView: TextView = itemView.cardmobile

    fun bind(post: Post) {
        authorView.text = post.name
        messageView.text = post.mobile.toString()
    }
}