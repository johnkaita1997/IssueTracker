package kaita.stream_app_final.Fragments.ProfileFragments

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Authentication.SignUpActivity
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.AppConstants.Constants.progressDialog
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_editprofile.*
import kotlinx.android.synthetic.main.fragment_editprofile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class EditProfile : Fragment() {

    private lateinit var source : View
    private val PICK_IMAGE_REQUEST = 1
    private var mImageUri: Uri? = null
    private var mStorageRef: StorageReference? = null

    @Nullable
    override fun onCreateView( inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle? ): View? {
        source = inflater.inflate(R.layout.fragment_editprofile, container, false)
        initall()
        return source
    }

    private fun initall() {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads").child(firebaseAuth.currentUser.uid);
        progressDialog = SpotsDialog.Builder().setContext(context).build() as SpotsDialog

        CoroutineScope(Dispatchers.IO).launch {
            load_Initial_Profile_Information()
        }

        edit_image_Listener()
        save_changes_Listener()
    }

    private fun save_changes_Listener() {
        source.save_Changes.setSafeOnClickListener {
            val name = edit_name.text.toString().trim()
            val email = edit_Email.text.toString().trim()
            val mobileNumber = edit_Mobile.text.toString().trim()
            val idnumber = "0000000000"
            if (name == "" || name == "None" || name == "null") {
                activity?.makeLongToast("You can't leave name field blank")
            } else if (email == "" || email == "None" || email == "null") {
                activity?.makeLongToast("You can't leave email field blank")
            } else if (mobileNumber == "" || mobileNumber == "None" || mobileNumber == "null") {
                activity?.makeLongToast("You can't leave Mobile field blank")
            } else {
                //save to Firebase
                if (!progressDialog.isShowing) { progressDialog.show()}
                progressDialog.setMessage("Saving your changes..")
                val userMap = HashMap<String, Any>()
                userMap["name"] = name
                userMap["email"] = email
                userMap["mobileNumber"] = mobileNumber
                userMap["idnumber"] = idnumber

                FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid).updateChildren(userMap)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        activity?.makeLongToast("Operation Successful")
                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                    } else {
                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                        activity?.showAlertDialog("Operation Incomplete, try again later: ${it.exception.toString()}")
                    }
                }
            }
        }
    }

    private fun edit_image_Listener() {
        source.edit_image.setSafeOnClickListener {
            openFileChooser();
        }
    }

    private fun load_Initial_Profile_Information() {
        FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid)?.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    activity?.makeLongToast("Register or Login First")
                    activity?.goToActivity(requireActivity(), SignUpActivity::class.java)
                } else {
                    //set to text
                    if (edit_name != null && edit_Email != null && edit_Mobile != null && edit_idnumber != null) {
                        var username = "Enter your both Names"
                        var email = "Enter your email"
                        var mobile = "Enter your mobile"
                        var dpurl = "None"
                        var idnumber = "None"
                        username = snapshot.child("name").value.toString()
                        email = snapshot.child("email").value.toString()
                        mobile = snapshot.child("mobileNumber").value.toString()
                        dpurl = snapshot.child("dpurl").value.toString()
                        if (snapshot.child("idnumber").exists()) {
                            idnumber = snapshot.child("idnumber").value.toString()
                        }

                        edit_name!!.setText(username)
                        edit_Email!!.setText(email)
                        edit_Mobile!!.setText(mobile)
                        edit_idnumber!!.setText(idnumber)

                        try {
                            if (requireActivity() != null) {
                                if (requireActivity().findViewById(R.id.top_Level_Name) as TextView != null) {
                                    val top_Level_Name = requireActivity().findViewById(R.id.top_Level_Name) as TextView
                                    top_Level_Name.text = username
                                }

                            }
                            //Set Image using Picasso.
                            if (dpurl != null && dpurl != "" && dpurl != "None") {
                                val imageView = requireActivity().findViewById(R.id.profile_image) as CircleImageView
                                Picasso
                                    .get()
                                    .load(dpurl)
                                    .placeholder(R.drawable.personb)
                                    .noFade()
                                    .into(imageView)
                            }
                        } catch (e: Exception) {
                            Log.d("EXCEPTION", "onDataChange: ${e.message.toString()}")
                        }

                    }
                }
            }
        })
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            val imageView = requireActivity().findViewById(R.id.profile_image) as CircleImageView
            mImageUri = data.getData();
            Picasso
                .get()
                .load(mImageUri)
                .placeholder(R.drawable.personb)
                .noFade()
                .into(imageView)
            uploadFile();
        }
    }

    private fun uploadFile() {
        if (mImageUri != null) {
            mStorageRef?.putFile(mImageUri!!)?.continueWithTask { task ->
                if (!task.isSuccessful) {throw task.exception!! }
                mStorageRef?.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid)?.child("dpurl")?.setValue(downloadUri.toString())?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            activity?.makeLongToast("Upload was successful")
                        } else {
                            activity?.makeLongToast("Operation Incomplete: ${it.exception?.message.toString()}")
                        }
                    }
                } else {
                    activity?.showAlertDialog("upload failed: " + task.exception!!.message)
                }
            }
        } else {
            activity?.makeLongToast("No file Selected")
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = activity?.contentResolver!!
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
}