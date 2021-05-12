package kaita.stream_app_final.Fragments.CreateStream

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.travijuu.numberpicker.library.NumberPicker
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.progressDialog
import kaita.stream_app_final.AppConstants.Constants.streams
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_closed.view.*
import kotlinx.android.synthetic.main.fragment_closed.view.save_A
import kotlinx.android.synthetic.main.fragment_closed.view.submit_Bet
import kotlinx.android.synthetic.main.fragment_open_ended.*
import kotlinx.android.synthetic.main.fragment_open_ended.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class OpenEnded : Fragment() {

    private lateinit var source: View
    val hashMap_Stream_Options = HashMap<String, Any>()
    val hashmap_Abcd = HashMap<String, Any>()
    lateinit var callbackManager: CallbackManager
    lateinit var shareDialog: ShareDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        source = inflater.inflate(R.layout.fragment_open_ended, container, false)
        initall()
        return source
    }

    private fun initall() {

        initialize_Facebook()

        progressDialog = SpotsDialog.Builder().setContext(requireContext()).build() as SpotsDialog

        source.submit_Bet.setSafeOnClickListener {
            val stream_tittle = requireActivity().findViewById(R.id.stream_tittle) as EditText
            val title = stream_tittle.text.toString().trim()

            val stream_description =
                requireActivity().findViewById(R.id.stream_description) as EditText
            val description = stream_description.text.toString().trim()

            val numberPicker: NumberPicker =
                requireActivity().findViewById(R.id.number_picker) as NumberPicker
            val joinnumber = numberPicker.value.toString().trim()

            FirebaseChecker().load_All {
                if (it.exists() and it.child("cashday").exists() and it.child("lastday").exists()) {

                    val last_day = it.child("lastday").value.toString()
                    val cash_day = it.child("cashday").value.toString()

                    // You can use any format to compare by spliting the dateTime
                    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")

                    val thelastday: String = last_day
                    val selectedlastday: Date = formatter.parse(thelastday)

                    val thecashday: String = cash_day
                    val selectedcashday: Date = formatter.parse(thecashday)

                    if (title == "" || description == "") {
                        requireActivity().showAlertDialog("Enter both the stream tittle and Stream description")
                    } else if (joinnumber == "") {
                        requireActivity().showAlertDialog("Select the Max Number Of Streamers who can join your stream")
                    } else if (last_day == "" || cash_day == "") {
                        requireActivity().showAlertDialog("Enter both the Last Day and Cash Out Day")
                    } else if (hashmap_Abcd.size < 1) {
                        requireActivity().showAlertDialog("You need to enter a betting option")
                    } else if (selectedcashday.compareTo(selectedlastday) < 0) {
                        requireActivity().makeLongToast("Last Voting should come before Cash Day")
                    } else if (selectedcashday.compareTo(selectedlastday) == 0) {
                        requireActivity().makeLongToast("Both days cannot be equal")
                    } else {
                        if (!progressDialog.isShowing) { progressDialog.show()}
                        FirebaseChecker().load_All {
                            var name = it.child(Constants.fname).value.toString()
                            var dpurl = it.child(Constants.fdpurl).value.toString()
                            hashMap_Stream_Options["title"] = title
                            hashMap_Stream_Options["description"] = description
                            hashMap_Stream_Options["joinnumber"] = joinnumber
                            hashMap_Stream_Options["lastday"] = last_day
                            hashMap_Stream_Options["cashday"] = cash_day
                            hashMap_Stream_Options["paid"] = "paid"
                            hashMap_Stream_Options["open"] = "open"
                            hashMap_Stream_Options["contribution"] = "0"
                            hashMap_Stream_Options["type"] = "Open"
                            hashMap_Stream_Options["host"] = Constants.firebaseAuth.currentUser.uid
                            hashMap_Stream_Options["maxoptions"] = number_Picker_max_Options.value.toString().trim()
                            hashMap_Stream_Options["host"] = Constants.firebaseAuth.currentUser.uid
                            hashMap_Stream_Options["hostimage"] = dpurl
                            hashMap_Stream_Options["hostname"] = name
                            val currentTimestamp = System.currentTimeMillis()
                            hashMap_Stream_Options["stamp"] = currentTimestamp
                            hashMap_Stream_Options["remove"] = "None"

                            checkWhetherToSharePublicly_Or_Privately()

                        }
                    }

                } else {
                    requireActivity().makeLongToast("Not all dates are set")
                }
            }
        }

        source.save_A.setSafeOnClickListener {
            if (hashMap_Stream_Options["a"] != null) {
                source.closed_edit_A.setText(hashMap_Stream_Options["a"].toString())
            }
        }

        listener_For_A()
    }

    private fun initialize_Facebook() {
        FacebookSdk.sdkInitialize(requireActivity())
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
    }

    private fun checkWhetherToSharePublicly_Or_Privately() {
        if (!progressDialog.isShowing) { progressDialog.show()}

        streams.child(Constants.firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                    requireActivity().showAlertDialog("You can only have one active stream at a time")
                } else {

                    // Set up the alert builder
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                    builder.setTitle("How do you want to share your stream")
                    val choicelist = arrayOf("Public", "Privately")
                    // set single choice items
                    builder.setSingleChoiceItems(choicelist,-1 ){dialog, i ->
                    }

                    builder.setPositiveButton("Proceed",
                        DialogInterface.OnClickListener { dialog, which ->
                            val position = (dialog as AlertDialog).listView.checkedItemPosition
                            if (position !=-1){
                                val selectedItem = choicelist[position]
                                hashMap_Stream_Options["howtoshare"] = selectedItem
                                now_Write_Db_Code(selectedItem)
                            }
                        })
                    builder.setNegativeButton("Cancel", null)
                    // Create and show the alert dialog
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (progressDialog.isShowing) {progressDialog.dismiss()}
                requireActivity().makeLongToast(error.message.toString())
            }
        })
    }

    private fun now_Write_Db_Code(selectedItem: String) {
        streams.child(Constants.firebaseAuth.currentUser.uid).setValue(hashMap_Stream_Options).addOnCompleteListener {
            if (it.isSuccessful) {
                hashmap_Abcd.forEach {
                    streams.child(Constants.firebaseAuth.currentUser.uid).child("options").child(it.key).child("name").setValue(it.value).addOnCompleteListener {
                        if (it.isSuccessful) {
                        } else {
                            if (progressDialog.isShowing) {progressDialog.dismiss()}
                        }
                    }
                }
                empty_edit_Text_Values(selectedItem)

            } else {
                if (progressDialog.isShowing) {progressDialog.dismiss()}
                requireActivity().showAlertDialog("Task Failed: ${it.exception.toString()}")
            }
        }
    }

    private fun listener_For_A() {
        source.register_A_Open.setSafeOnClickListener {
            val selected_Option = source.closed_edit_A_OpenEnded.text.toString().trim()
            if (selected_Option != "") {
                show_Image_Saved(question_Mark_Open_A)
                hashmap_Abcd["a"] = selected_Option
            } else {
                requireActivity().makeLongToast("Enter the option first")
            }
        }
        source.remove_A_Open.setSafeOnClickListener {
            show_option_Removed(question_Mark_Open_A)
            hashmap_Abcd.remove("a")
            source.closed_edit_A_OpenEnded.setText("")
        }
    }

    private fun show_option_Removed(imageView: ImageView?) {imageView?.setImageResource(R.drawable.questionmark)}
    private fun show_Image_Saved(imageView: ImageView?) { imageView?.setImageResource(R.drawable.greentick)}

    private fun empty_edit_Text_Values(selectedItem: String) {

        val stream_tittle = requireActivity().findViewById(R.id.stream_tittle) as EditText
        val stream_description = requireActivity().findViewById(R.id.stream_description) as EditText
        stream_description.setText("")
        stream_tittle.setText("")
        source.closed_edit_A_OpenEnded.setText("")
        if (progressDialog.isShowing) {progressDialog.dismiss()}

        // Set up the alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Your stream was posted successfully. Share this Stream to your friends")
        builder.setPositiveButton("Whatsapp",
            DialogInterface.OnClickListener { dialog, which ->
                FirebaseChecker().load_selected_Streamer_Stream(Constants.firebaseAuth.currentUser.uid){
                    if (it.exists()) {

                        var strAppLink = ""
                        val appPackageName = requireContext().getPackageName();
                        strAppLink = try {
                            "https://play.google.com/store/apps/details?id=$appPackageName"
                        } catch (anfe: ActivityNotFoundException) {
                            "https://play.google.com/store/apps/details?id=$appPackageName"
                        }

                        val title = it.child("title").value.toString()
                        val mybet = it.child("contribution").value.toString()

                        var bettmessage = "Lets bet now !!\nTitle: $title\nMy Bet: $mybet\nGet Stream App: $strAppLink\nBet Link: https://www.worldstream.co.ke/streamed/joinbet.php?id=${Constants.firebaseAuth.currentUser.uid}"

                        val a = Intent(Intent.ACTION_SEND)
                        // this is the sharing part
                        a.type = "text/link"
                        val shareBody = bettmessage.trimIndent()
                        val shareSub = "Stream Bet"
                        a.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                        a.putExtra(Intent.EXTRA_TEXT, shareBody)
                        startActivity(Intent.createChooser(a, "Share Using"))

                    } else {
                        requireActivity().makeLongToast("Important information missing")
                    }
                }
                dialog.dismiss()
            })
        builder.setNegativeButton("Facebook",
            DialogInterface.OnClickListener { dialog, which ->
                shareDialog.registerCallback(callbackManager, object :
                    FacebookCallback<Sharer.Result> {
                    override fun onSuccess(result: Sharer.Result?) {
                        requireActivity().makeLongToast("Your Stream was shared to Facebook")
                    }
                    override fun onCancel() {
                        requireActivity().makeLongToast("Sharing Cancelled")
                    }
                    override fun onError(error: FacebookException?) {
                        requireActivity().makeLongToast(error?.message.toString())
                    }
                })

                val linkContent = ShareLinkContent.Builder()
                    .setQuote("Facebook Share API Test Link - Random")
                    //.setContentUrl(Uri.parse("https://www.worldstream.co.ke/streamed/joinbet.php?id=${firebaseAuth.currentUser.uid}"))
                    .setContentUrl(Uri.parse("https://www.google.com/"))
                    .build()

                if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                    shareDialog.show(linkContent)
                } else {
                    requireActivity().makeLongToast("Ensure you have the Facebook App installed to share this Stream")
                }
                dialog.dismiss()
            })
        builder.setNeutralButton("Dismiss", null)
        // Create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }
}