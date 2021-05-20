package kaita.stream_app_final.Adapteres.Complaints

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R


class ComplainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var name: TextView = itemView.findViewById(R.id.c_Name)
    private var email: TextView = itemView.findViewById(R.id.c_Email)
    private var mobile: TextView = itemView.findViewById(R.id.c_Mobile)
    private var thecomplaint: TextView = itemView.findViewById(R.id.c_thecomplaint)
    private var delete: Button = itemView.findViewById(R.id.c_delete_Complaint)
    private var call: Button = itemView.findViewById(R.id.c_call_Client)

    fun bind( complaining: Complained, viewHolder: ComplainViewHolder, theactivity: FragmentActivity, databaseRerence: DatabaseReference) {

        name.text = "${complaining.name}"
        email.text = "${complaining.email}"
        mobile.text = "${complaining.mobile}"
        thecomplaint.text = "${complaining.complain}"

        delete.setSafeOnClickListener {

            val databaseRerence = databaseRerence
            databaseRerence.removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            theactivity.makeLongToast("Complained was removed")
                            val query = FirebaseDatabase.getInstance().reference.child("complains");
                            val options = FirebaseRecyclerOptions.Builder<Complained>()
                                .setQuery(query, Complained::class.java)
                                .build()
                            Constants.complainsAdapter.notifyDataSetChanged()

                        } else {
                            theactivity.makeLongToast("Error: ${it.exception.toString()}")
                        }
                    }
        }

        call.setSafeOnClickListener {
            val call_Intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "$mobile"))
            if (ActivityCompat.checkSelfPermission( theactivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                theactivity.makeLongToast("Go to App Settings, Stream, Storage, Permissions and Grant Make Call Permission")
                val permission_Intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:" + theactivity.packageName))
                theactivity.startActivity(permission_Intent)
            } else {
                theactivity.startActivity(call_Intent)
            }
        }
    }
}
