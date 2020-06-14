package smartherd.kenyamessagesolution.Adapteres

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import smartherd.kenyamessagesolution.R

class HobbiesAdapter(val context: Context, private val hobbies: ArrayList<String>) :
    RecyclerView.Adapter<HobbiesAdapter.myViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        //Combine the data with the views so instead of getting my title, we get the list of hobbies
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return myViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hobbies.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        //From the list of objects already bound, let us get the position of the items
        val hobby = hobbies[position]
        holder.setData(hobby, position)
    }

    //In order to bing the data to each of the view, we need to have a ViewHolder class
    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Define the current hobby being clicked along with its position
        var currentHobby: String = ""
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {

                currentHobby.let {
                    Toast.makeText(context, currentHobby + " clicked.", Toast.LENGTH_LONG)
                        .show()
                }
            }

            itemView.imgShare.setOnClickListener {

                currentHobby.let {
                    val message = "My hobby is : ${currentHobby}"

                    //Go to a different application
                    val intent = Intent()
                    //Define what we will be sharing
                    intent.putExtra(Intent.EXTRA_TEXT, message)
                    intent.type = "text/plain"
                    context.startActivity(Intent.createChooser(intent, "Share to..."))
                }

            }
        }

        fun setData(hobby: String, position: Int) {

            hobby.let {
                itemView.txvTitle.text = hobby

                this.currentHobby = hobby
                this.currentPosition = position
            }
        }
    }

}

