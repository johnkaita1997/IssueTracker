@file:Suppress("NAME_SHADOWING")
package Fragments

import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.android.synthetic.main.fragment_one_compose.*
import kotlinx.android.synthetic.main.fragment_one_compose.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smartherd.kenyamessagesolution.AppConstants.Constants.EMAIL
import smartherd.kenyamessagesolution.AppConstants.Constants.PASSWORD
import smartherd.kenyamessagesolution.AppExecutors
import smartherd.kenyamessagesolution.Extensions.makeLongToast
import smartherd.kenyamessagesolution.Extensions.showAlertDialog
import smartherd.kenyamessagesolution.R
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class OneCompose : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var databaseHome: CollectionReference
    private lateinit var db: FirebaseFirestore
    private lateinit var viewy: View
    private var themessage = ""
    private lateinit var menu: PopupMenu
    private var groupName = ""
    private lateinit var menusecond: PopupMenu
    private var mArrayList: ArrayList<Any> = ArrayList()
    lateinit var appExecutors: AppExecutors
    private lateinit var auth: FirebaseAuth
    private var theCounter = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewy = inflater.inflate(R.layout.fragment_one_compose, container, false)

        initall()

        return viewy
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initall() {

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        appExecutors = AppExecutors()
        databaseHome = db.collection(auth.currentUser!!.email.toString()).document("Information").collection("People")

        viewy.sendtoallcontacts.setOnClickListener {
            //Validate the message box first.
            themessage = message.text.toString().trim()
            if (themessage.equals("")) {
                activity?.makeLongToast("You cannot leave the message blank")
            } else {
                sendSmsToAllContacts(themessage)
            }
        }

        viewy.groupButton.setOnClickListener {
            menu = PopupMenu(activity, it)
            //Validate the message box first.
            themessage = message.text.toString().trim()
            if (themessage.equals("")) {
                activity?.makeLongToast("You cannot leave the message blank")
            } else {
                sendGroupMessage(themessage)
            }
        }

        viewy.insert.setOnClickListener {
            //load the values and add them to the edit text.
            menusecond = PopupMenu(activity, it)
            loadValuesToEditText()
        }

    }

    private fun sendGroupMessage(@Suppress("UNUSED_PARAMETER") themessage: String) {
        //Load the groups
        var groupDatabaseHome: CollectionReference
        groupDatabaseHome = db.collection(auth.currentUser!!.email.toString()).document("Groups").collection("GroupList")
        groupDatabaseHome
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        //Add the items to the menu
                        menu.menu.add(document.id.toString())

                    } catch (e: Exception) {
                        activity?.showAlertDialog("An error occured \n $e")
                        break
                    }
                    menu.setOnMenuItemClickListener(this)
                    menu.show()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("mate", "Error getting documents: ", exception)
                activity?.makeLongToast("Operation incomplete, an internal error has occured")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessageToAll(themessage: String, selected: String) {
        databaseHome
            .get()
            .addOnSuccessListener { result ->
                var mobile: Int
                var themobile: String = ""
                var theemail = ""
                lateinit var thedocument: QueryDocumentSnapshot

                for (document in result) {
                    try {
                        Log.d("mate", "${document.id} => ${document.data.get("mobile").toString()}")

                        thedocument = document

                        if (!document.data.get("mobile")!!.equals(null)) {
                            mobile = document.data.get("mobile").toString().toInt()
                            themobile = mobile.toString()
                        }

                        if (!document.data.get("email")!!.equals(null)) {
                            theemail = document.data.get("email").toString().trim()
                        }

                        if (selected.equals("Email")) {
                            sendEmailMessage_AllContacts(
                                themobile,
                                themessage,
                                thedocument,
                                theemail
                            )
                        } else if (selected.equals("Sms")) {
                            sendFinalSmsMessage_AllContacts(
                                themobile,
                                themessage,
                                thedocument,
                                theemail
                            )
                        }

                    } catch (e: Exception) {
                        activity?.showAlertDialog("An error occured \n $e")
                        break
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("mate", "Error getting documents: ", exception)
                activity?.makeLongToast("Operation incomplete, an internal error has occured")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun SendMessageToThisGroup(
        themessage: String,
        groupName: String,
        selected: String
    ) {
        //Load the groups
        var groupDatabaseHome: DocumentReference
        groupDatabaseHome =
            db.collection(auth.currentUser!!.email.toString()).document("Groups").collection("GroupList").document(groupName)
        groupDatabaseHome
            .get()
            .addOnSuccessListener { result ->
                //Get the results
                var whereAll = result.get("whereall").toString()
                var startswith = result.get("startswith").toString()
                Log.d("mate", whereAll + ".........." + startswith)

                //Load to all collection and get this query back from it.
                db.collection(auth.currentUser!!.email.toString()).document("Information")
                    .collection("People")
                    .whereEqualTo(whereAll.trim(), startswith.trim())
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            for (document in result) {
                                try {

                                    var themobile = ""
                                    var theemail = ""

                                    if (!document.data.get("email").toString()
                                            .equals(null) && !document.data.get("mobile")!!
                                            .equals(null)
                                    ) {
                                        theemail = document.data.get("email").toString()
                                        themobile = document.data.get("mobile").toString()
                                    }

                                    if (selected.equals("Email")) {
                                        sendEmailMessage(
                                            themobile,
                                            themessage,
                                            document.data,
                                            theemail
                                        )
                                    } else if (selected.equals("Sms")) {
                                        sendFinalSmsMessage(
                                            themobile,
                                            themessage,
                                            document.data,
                                            theemail
                                        )
                                    }
                                } catch (e: Exception) {
                                    activity?.showAlertDialog("An error occured \n $e")
                                    break
                                }
                            }
                        } else {
                            activity?.makeLongToast("No such document exists")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("mate", "Error getting documents: ", exception)
                        activity?.makeLongToast("Operation incomplete, an internal error has occured")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("mate", "Error getting documents: ", exception)
                activity?.makeLongToast("Operation incomplete, an internal error has occured")
            }
    }

    private fun loadValuesToEditText() {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(auth.currentUser!!.email.toString()).document("Keys")

        // do something
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val deviceNameList = arrayListOf<String>()
                Log.d("mate", "DocumentSnapshot data: ${document.data?.get("allkeys").toString()}")
                mArrayList = arrayListOf(document.data?.get("allkeys")) as  @Suppress("UNCHECKED_CAST")  ArrayList<Any>

                if (!mArrayList.isEmpty()) {
                    mArrayList.forEach {
                        if (!it.equals("")) {
                            deviceNameList.add(it.toString())
                        }else {
                            activity?.makeLongToast("No data available")
                        }
                    }

                    val input = deviceNameList[0]
                    val names: Array<String> = input.substring(1, input.length - 1).split(",\\s*").toTypedArray()

                    //This prints all the values in an array
                    for (x in names[0].split(",")) {
                        menusecond.menu.add(x)
                    }

                    menusecond.setOnMenuItemClickListener(object :
                        PopupMenu.OnMenuItemClickListener {
                        override fun onMenuItemClick(item: MenuItem): Boolean {
                            //How do i add
                            var secondgroupName = item.title.toString().trim()
                            insertintoedittext(secondgroupName)
                            return true
                        }
                    })

                    menusecond.show()
                } else {
                    activity?.makeLongToast("No data available")
                }

            } else {
                Log.d("mate", "No such document")
                activity?.makeLongToast("You haven't added this data")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("mate", "get failed with ", exception)
            }
    }

    private fun insertintoedittext(secondgroupName: String) {
        val start: Int = message.getSelectionStart()    //this is to get the the cursor position
        message.getText().insert(
            start, "$" + secondgroupName
        )               //this will get the text and insert the String s into   the current position
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun main(@Suppress("UNUSED_PARAMETER")args: Array<String>) {

        val message = "You purchased \$name on \$date"
        val arr = message.split(" ").toTypedArray()
        for (i in arr.indices) {
            val s = arr[i]
            if (s.contains("$")) {
                arr[i] = "+ querySnapshot.get(" + "\"" + s.substring(1) + "\"" + ")"
            }
        }
        println(java.lang.String.join(" ", *arr))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        //How do i add
        groupName = item?.title.toString()
        activity?.makeLongToast("Group was selected")
        sendMessageGroupName(groupName)
        return true
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessageGroupName(groupName: String) {
        //Validate the message box first.
        themessage = message.text.toString().trim()
        if (themessage.equals("")) {
            activity?.makeLongToast("You cannot leave the message blank")
        } else {
            sendSmsToAGroup(groupName, themessage)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendSmsToAGroup(groupName: String, themessage: String) {
        //Select the sign up optionn
        val signup_options = arrayOf("Email", "Sms")
        val builder = AlertDialog.Builder(this.activity!!)
        builder.setTitle("Select Message Type")
        builder.setItems(signup_options) { dialog, selected_option ->
            val selected = signup_options[selected_option]
            if (selected.equals("Email")) {
                SendMessageToThisGroup(themessage, groupName, selected)
                dialog.dismiss()
            } else if (selected.equals("Sms")) {
                SendMessageToThisGroup(themessage, groupName, selected)
                dialog.dismiss()
            }
        }
        builder.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendSmsToAllContacts(themessage: String) {
        //Select the sign up optionn
        val signup_options = arrayOf("Email", "Sms")
        val builder = AlertDialog.Builder(this.activity!!)
        builder.setTitle("Select Message Type")
        builder.setItems(signup_options) { dialog, selected_option ->
            val selected = signup_options[selected_option]
            if (selected.equals("Email")) {
                sendMessageToAll(themessage, selected)
                dialog.dismiss()
            } else if (selected.equals("Sms")) {
                sendMessageToAll(themessage, selected)
                dialog.dismiss()
            }
        }
        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendFinalSmsMessage(
        theMobile: String,
        themessage: String,
        data: MutableMap<String, Any>,
        @Suppress("UNUSED_PARAMETER")theemail: String
    ) {
        Log.d("mate", theMobile)

        val message = themessage
        val arr = message.split(" ").toTypedArray()

        for (i in 0 until arr.size) {
            val s = arr[i]
            if (s.contains("$")) {
                //arr[i] = "+ querySnapshot.get(" + "\"" + s.substring(1) + "\"" + ")"
                arr[i] = data.get(s.substring(1)).toString()
            }
        }

        var theMessaged = java.lang.String.join(" ", *arr)
        Log.d("Mate", java.lang.String.join(" ", *arr))

        if (!theMobile.equals("")) {
            CoroutineScope(Dispatchers.IO).launch {
                itsoverSMS(theMobile, theMessaged)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendFinalSmsMessage_AllContacts(
        themobile: String,
        @Suppress("UNUSED_PARAMETER")themessage: String,
        document: QueryDocumentSnapshot,
        @Suppress("UNUSED_PARAMETER")theemail: String
    ) {
        val message = this.themessage
        val arr = message.split(" ").toTypedArray()
        for (i in 0 until arr.size) {
            val s = arr[i]
            if (s.contains("$")) {
                //arr[i] = "+ querySnapshot.get(" + "\"" + s.substring(1) + "\"" + ")"
                arr[i] = document.get(s.substring(1)).toString()
            }
        }
        var theMessaged = java.lang.String.join(" ", *arr)
        Log.d("Mate", java.lang.String.join(" ", *arr))

        if (!themobile.equals("")) {
            CoroutineScope(Dispatchers.IO).launch {
                itsoverSMS(themobile, theMessaged)
            }
        }
    }

    private suspend fun itsoverSMS(themobile: String, themessage: String) {
        if (theCounter <= 0) {
            activity!!.runOnUiThread {
                activity!!.makeLongToast("Congradulations. Messages will continue sending in background.")
            }
            theCounter++
        }

        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(themobile, null, themessage, null, null)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendEmailMessage_AllContacts(
        @Suppress("UNUSED_PARAMETER")themobile: String,
        @Suppress("UNUSED_PARAMETER")themessage: String,
        document: QueryDocumentSnapshot,
        theemail: String
    ) {
        val message = this.themessage
        val arr = message.split(" ").toTypedArray()
        for (i in 0 until arr.size) {
            val s = arr[i]
            if (s.contains("$")) {
                //arr[i] = "+ querySnapshot.get(" + "\"" + s.substring(1) + "\"" + ")"
                arr[i] = document.get(s.substring(1)).toString()
            }
        }

        var theMessaged = java.lang.String.join(" ", *arr)
        Log.d("Mate", java.lang.String.join(" ", *arr))

        if (!theemail.equals("")) {
            CoroutineScope(Dispatchers.IO).launch {
                itsoverEmail(theemail, theMessaged)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendEmailMessage(
        theMobile: String,
        themessage: String,
        data: MutableMap<String, Any>,
        theemail: String
    ) {
        //Send using email
        Log.d("mate", theMobile)
        val message = themessage
        val arr = message.split(" ").toTypedArray()

        for (i in 0 until arr.size) {
            val s = arr[i]
            if (s.contains("$")) {
                //arr[i] = "+ querySnapshot.get(" + "\"" + s.substring(1) + "\"" + ")"
                arr[i] = data.get(s.substring(1)).toString()
            }
        }

        var theMessaged = java.lang.String.join(" ", *arr)
        Log.d("Mate", java.lang.String.join(" ", *arr))

        if (!theemail.equals("")) {
            CoroutineScope(Dispatchers.IO).launch {
                itsoverEmail(theemail, theMessaged)
            }
        }
    }

    private suspend fun itsoverEmail(theemail: String, themessage: String) {
        if (theCounter <= 0) {
            activity!!.runOnUiThread {
                activity!!.makeLongToast("Congradulations. Messages will continue sending in background.")
            }
            theCounter++
        }

        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL, PASSWORD)
                    }
                })

            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                val emailId = theemail
                //Setting sender address
                mm.setFrom(InternetAddress(EMAIL))
                //Adding receiver
                mm.addRecipient(Message.RecipientType.TO, InternetAddress(emailId))
                //Adding subject
                mm.subject = auth.currentUser!!.uid
                //Adding message
                mm.setText(themessage)
                //Sending email
                Transport.send(mm)
                appExecutors.mainThread()
                    .execute { //Something that should be executed on main thread.
                    }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }
}
