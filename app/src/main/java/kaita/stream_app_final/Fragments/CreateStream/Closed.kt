package kaita.stream_app_final.Fragments.CreateStream

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.onesignal.OneSignal
import com.travijuu.numberpicker.library.NumberPicker
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Normal.PostActivity
import kaita.stream_app_final.Adapteres.CreatePost.CreatePost
import kaita.stream_app_final.Adapteres.CreatePost.CreatePostViewHolder
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.alist
import kaita.stream_app_final.AppConstants.Constants.database
import kaita.stream_app_final.AppConstants.Constants.fdpurl
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.AppConstants.Constants.fname
import kaita.stream_app_final.AppConstants.Constants.progressDialog
import kaita.stream_app_final.AppConstants.Constants.selected_id
import kaita.stream_app_final.AppConstants.Constants.streams
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_closed.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class Closed : Fragment(){

    private lateinit var source : View
    val hashMap_Stream_Options = HashMap<String, Any>()
    val hashmap_Abcd = HashMap<String, String>()
    lateinit var callbackManager: CallbackManager
    lateinit var shareDialog: ShareDialog
    lateinit var popup : PopupMenu

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        source =  inflater.inflate(R.layout.fragment_closed, container, false)
        initall()
        return source

    }

    private fun initall() {

        load_Alphabets()
        progressDialog = SpotsDialog.Builder().setContext(requireContext()).build() as SpotsDialog

        initialize_Facebook()

        source.submit_Bet.setSafeOnClickListener {
            val stream_tittle = requireActivity().findViewById(R.id.stream_tittle) as EditText
            val title = stream_tittle.text.toString().trim()

            val stream_description = requireActivity().findViewById(R.id.stream_description) as EditText
            val description = stream_description.text.toString().trim()

            val numberPicker: NumberPicker = requireActivity().findViewById(R.id.number_picker) as NumberPicker
            val joinnumber = numberPicker.value.toString().trim()

            val last_day = (activity as PostActivity?)?.get_Last_Day().toString()
            val cash_day = (activity as PostActivity?)?.get_Cash_Day().toString()

            // You can use any format to compare by spliting the dateTime
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")

            val thelastday: String = last_day
            val selectedlastday: Date = formatter.parse(thelastday)

            val thecashday: String = cash_day
            val selectedcashday: Date = formatter.parse(thecashday)

            val comparision = last_day.compareTo(cash_day)

            if (title == "" || description == "") {
                requireActivity().showAlertDialog("Enter both the stream subject and Stream description")
            } else if (joinnumber == "") {
                requireActivity().showAlertDialog("Select the Max Number Of Streamers who can join your stream")
            } else if (last_day == "" || cash_day == "") {
                requireActivity().showAlertDialog("Enter both the Last Day and Cash Out Day")
            } else if (hashmap_Abcd.size < 2 ) {
                requireActivity().showAlertDialog("You need to enter at least 2 options")
            } else if (selectedcashday.compareTo(selectedlastday) < 0) {
                requireActivity().makeLongToast("Last Voting should come before Cash Day")
            }else if (comparision == 0) {
                requireActivity().showAlertDialog("Cash day and final betting day can't be equal")
            }else {
                popup = PopupMenu(requireActivity(), it)
                //Load firebase menu now
                FirebaseDatabase.getInstance().reference.child("categories").addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                                requireActivity().makeLongToast("Error: ${error.message}")
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists() && snapshot.hasChildren()) {
                                    for (value in snapshot.children) {
                                        val category_Option = value.child("name").value.toString()
                                        popup.menu.add(category_Option)
                                    }
                                } else {
                                    requireActivity().makeLongToast("Error!, missing info for categories.")
                                }

                                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                                        val entered_Category = item!!.title.toString()

                                        if (!progressDialog.isShowing) { progressDialog.show()}
                                        FirebaseChecker().load_All {

                                            var name = it.child(fname).value.toString()
                                            var dpurl = it.child(fdpurl).value.toString()

                                            hashMap_Stream_Options["title"] = title.capitalize()
                                            hashMap_Stream_Options["description"] = description.capitalize()
                                            hashMap_Stream_Options["joinnumber"] = joinnumber
                                            hashMap_Stream_Options["category"] = entered_Category
                                            hashMap_Stream_Options["lastday"] = last_day
                                            hashMap_Stream_Options["cashday"] = cash_day
                                            hashMap_Stream_Options["paid"] = "paid"
                                            hashMap_Stream_Options["open"] = "open"
                                            hashMap_Stream_Options["contribution"] = "0"
                                            hashMap_Stream_Options["type"] = "Closed"
                                            hashMap_Stream_Options["host"] = firebaseAuth.currentUser.uid
                                            hashMap_Stream_Options["hostimage"] = dpurl
                                            hashMap_Stream_Options["hostname"] = name
                                            val currentTimestamp = System.currentTimeMillis()
                                            hashMap_Stream_Options["stamp"] = currentTimestamp.toString()
                                            hashMap_Stream_Options["remove"] = "None"
                                            hashMap_Stream_Options["order"] = -1 * Date().getTime()

                                            val formatter = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z")
                                            val date = Date(System.currentTimeMillis())
                                            val good_Date = formatter.format(date)
                                            hashMap_Stream_Options["postedon"] = good_Date.toString()

                                            do_Everything_DatabaseO_Related(currentTimestamp, title)
                                        }

                                        return false
                                    }
                                })

                                popup.show()

                            }
                        })
            }
        }
    }

    private fun initialize_Facebook() {
        FacebookSdk.sdkInitialize(requireActivity())
        callbackManager = CallbackManager.Factory.create()
        shareDialog = ShareDialog(this)
    }

    private fun do_Everything_DatabaseO_Related(currentTimestamp: Long, title: String) {

        val builderSingle = AlertDialog.Builder(requireActivity())
        builderSingle.setIcon(R.drawable.mainicon)
        builderSingle.setTitle("Confirm These Are The Options")
        builderSingle.setCancelable(false)

        //val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.select_dialog_singlechoice)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1)
        hashmap_Abcd.forEach {
            arrayAdapter.add("${it.key.capitalize()} -> ${it.value}")
        }

        builderSingle.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss()
        if (progressDialog.isShowing) {progressDialog.dismiss()}}
        builderSingle.setPositiveButton("Proceed") { dialog, which -> dialog.dismiss()
            actual_Database_Code(currentTimestamp, title)
        }
        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
           /* val strName: String = arrayAdapter.getItem(which).toString()
            val builderInner = AlertDialog.Builder(requireActivity())
            builderInner.setMessage(strName)
            builderInner.setTitle("Your Selected Item is")
            builderInner.setPositiveButton( "Ok") { dialog, which -> dialog.dismiss()
            }
            builderInner.show()*/
        }
        builderSingle.show()
    }

    private fun actual_Database_Code(currentTimestamp: Long, title: String) {
        streams.child(firebaseAuth.currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
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
                                now_Write_Db_Code(selectedItem, currentTimestamp, title)
                            }
                        })
                    builder.setNegativeButton("Cancel", null)
                    // Create and show the alert dialog
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
            }

            override fun onCancelled(error: DatabaseError) {
                if (progressDialog.isShowing) {progressDialog.dismiss()}
                requireActivity().makeLongToast(error.message.toString())
            }
        })
    }

    private fun now_Write_Db_Code(selectedItem: String, currentTimestamp: Long, title: String) {
        //Add to firestore
        val ref: DocumentReference = database.collection("streams").document(currentTimestamp.toString())
        ref.set(hashMap_Stream_Options).addOnCompleteListener {
            if (it.isSuccessful) {
                hashmap_Abcd.forEach {

                    val addMap = java.util.HashMap<String, String>()
                    addMap["name"] = it.value

                    val reference_two: DocumentReference =
                        database.collection("streams/${currentTimestamp}/options")
                            .document(it.key)
                    reference_two.set(addMap).addOnCompleteListener {
                        if (it.isSuccessful) {
                        } else {
                            requireActivity().makeLongToast("Error: ${it.exception.toString()}")
                        }
                    }
                }

                val separate_hashmap = HashMap<String, String>()
                separate_hashmap["title"] = title
                separate_hashmap["stamp"] = currentTimestamp.toString()

                //Add that to people keys
                FirebaseDatabase.getInstance().reference.child("keys")
                    .child(currentTimestamp.toString()).setValue(firebaseAuth.currentUser.uid)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            FirebaseDatabase.getInstance().reference.child("ids")
                                .child(firebaseAuth.currentUser.uid).child(currentTimestamp.toString())
                                .setValue(separate_hashmap).addOnCompleteListener {
                                if (it.isSuccessful) {

                                    streams.child(currentTimestamp.toString())
                                        .setValue(hashMap_Stream_Options).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            hashmap_Abcd.forEach {
                                                Constants.streams.child(currentTimestamp.toString())
                                                    .child("options").child(it.key).child("name")
                                                    .setValue(it.value).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                    } else {
                                                        if (progressDialog.isShowing) {
                                                            progressDialog.dismiss()
                                                        }
                                                        requireActivity().showAlertDialog("Task Failed ${it.exception.toString()}")
                                                    }
                                                }
                                            }
                                            empty_edit_Text_Values(currentTimestamp.toString())

                                        } else {
                                            if (progressDialog.isShowing) {
                                                progressDialog.dismiss()
                                            }
                                            requireActivity().showAlertDialog("Task Failed: ${it.exception.toString()}")
                                        }
                                    }

                                } else {
                                    requireActivity().makeLongToast("Incomplete: ${it.exception.toString()}")
                                }
                            }
                        } else {
                            requireActivity().makeLongToast("Error: ${it.exception.toString()}")
                        }
                    }

            } else {
                requireActivity().makeLongToast("Incomplete: ${it.exception.toString()}")
            }
        }

    }

    private fun empty_edit_Text_Values(currenttimestamp: String) {
        val stream_tittle = requireActivity().findViewById(R.id.stream_tittle) as EditText
        val stream_description = requireActivity().findViewById(R.id.stream_description) as EditText
        stream_description.setText("")
        stream_tittle.setText("")
        source.closed_edit_B.setText("")
        source.closed_edit_C.setText("")
        source.closed_edit_D.setText("")
        OneSignal.sendTag(currenttimestamp, currenttimestamp)
        if (progressDialog.isShowing) {progressDialog.dismiss()}
        // Set up the alert builder
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Your stream was posted successfully. To place a bet on it, visit Your Streams Page in the Profile Section. Share this Stream to your friends")
        builder.setPositiveButton("Others",
            DialogInterface.OnClickListener { dialog, which ->
                FirebaseChecker().load_selected_Streamer_Stream(currenttimestamp){
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

                        var bettmessage = "Join My Bet !!\nTitle: $title\nGet Stream App: $strAppLink\nBet Link: https://www.worldstream.co.ke/streamed/joinbet.php?id=${currenttimestamp.toString()}"
                        requireActivity().finish()
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
                FirebaseChecker().load_selected_Streamer_Stream(currenttimestamp) {
                    if (it.exists()) {
                        var strAppLink = ""
                        val appPackageName = requireActivity().packageName
                        strAppLink = try {
                            "https://play.google.com/store/apps/details?id=$appPackageName"
                        } catch (anfe: ActivityNotFoundException) {
                            "https://play.google.com/store/apps/details?id=$appPackageName"
                        }

                        val title = it.child("title").value.toString()
                        val mybet = it.child("contribution").value.toString()

                        val message = "Join My Bet!!\nTitle: $title\nGet Stream App: $strAppLink\nMy Bet Link: https://www.worldstream.co.ke/streamed/joinbet.php?id=${currenttimestamp}"

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
                            .setQuote(message)
                            .setContentUrl(Uri.parse("https://www.worldstream.co.ke/streamed/joinbet.php?id=${selected_id}"))
                            .build()

                        if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                            shareDialog.show(linkContent)
                        } else {
                            requireActivity().makeLongToast("Ensure you have the Facebook App installed to share this Stream")
                        }
                        dialog.dismiss()
                    }
                }
            })
        // Create and show the alert dialog
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    fun isPackageExisted(targetPackage: String?): Boolean {
        val pm: PackageManager = requireActivity().packageManager
        try {
            val info: PackageInfo = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    private fun load_Alphabets() {
        source.createPost_RecyclerView.setHasFixedSize(true)
        val numberOfColumns = 1
        val mManager = GridLayoutManager(requireActivity(), numberOfColumns)
        mManager.orientation = RecyclerView.HORIZONTAL
        source.createPost_RecyclerView.setLayoutManager(mManager)

        val peopleReference: DatabaseReference = FirebaseDatabase.getInstance().getReference().child("alphabets")
        val options: FirebaseRecyclerOptions<CreatePost?> = FirebaseRecyclerOptions.Builder<CreatePost>()
            .setQuery(peopleReference, CreatePost::class.java)
            .build()
        Constants.createPostAdapter = object : FirebaseRecyclerAdapter<CreatePost, CreatePostViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreatePostViewHolder {
                val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_create_post, parent, false)
                return CreatePostViewHolder(view)
            }
            override fun onBindViewHolder(viewholder: CreatePostViewHolder, position: Int, createpost: CreatePost) {
                viewholder.bind(createpost, viewholder, requireActivity(), hashmap_Abcd)
            }
        }
        source.createPost_RecyclerView.adapter = Constants.createPostAdapter
    }

    override fun onStart() {
        super.onStart()
        Constants.createPostAdapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        if (Constants.createPostAdapter != null) {
            Constants.createPostAdapter.stopListening();
        }
        Constants.chosen_Answer = ""
        alist.clear()
    }
}