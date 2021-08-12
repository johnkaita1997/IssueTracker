package kaita.stream_app_final.Adapteres.CreatePost

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants.alist
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R

class CreatePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val people_List = mutableListOf<String>()
    private var title: TextView = itemView.findViewById(R.id.b_title)
    private var choice: EditText = itemView.findViewById(R.id.b_choice)
    private var saveOption: Button = itemView.findViewById(R.id.b_save_option)

    fun bind(createpost: CreatePost, viewHolder: CreatePostViewHolder,theactivity: FragmentActivity,hashmapAbcd: HashMap<String, String> ) {

        title.text = "Option : ${createpost.name!!.toUpperCase()}"
        saveOption.setText("Save ${createpost.name!!.toUpperCase()}")
        saveOption.setHint("Enter Option ${createpost.name!!.toUpperCase()} - Max 70 characters")

        saveOption.setSafeOnClickListener {
            val user_Option = choice.text.toString().trim()
            if (user_Option == "") {
                theactivity.makeLongToast("Enter Option ${createpost.name!!.toUpperCase()} first")
            } else {
                if (alist.contains(user_Option)) {
                    theactivity.makeLongToast("You cannot have two similar options")
                } else {
                    hashmapAbcd["${createpost.name}"] = user_Option
                    theactivity.showAlertDialog("Option $user_Option has been saved, swap right or left to add other options")
                    alist.add(user_Option)
                }
            }
        }
    }
}
