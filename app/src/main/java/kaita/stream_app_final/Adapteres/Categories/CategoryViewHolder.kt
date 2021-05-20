package kaita.stream_app_final.Adapteres.Categories

import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.R

class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var selectCategory: TextView = itemView.findViewById(R.id.k_discover)

    fun bind(person: Category, viewHolder: CategoryViewHolder, theactivity: FragmentActivity) {
        selectCategory.text = "${person.name}"
        val thecategory  = person.name
        selectCategory.setSafeOnClickListener {
            val query = Constants.database
                .collection("streams")
                .whereEqualTo("category", thecategory)
            val options = FirestorePagingOptions.Builder<Post>()
                .setQuery(query, Constants.config, Post::class.java)
                .build()
            // Change options of adapter.
            Constants.mAdapter.updateOptions(options)
        }
    }
}
