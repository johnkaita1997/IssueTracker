package kaita.stream_app_final.Activities.Normal

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.OnButtonClickListener
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException
import com.onesignal.OneSignal
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import kaita.stream_app_final.Activities.Authentication.SignUpActivity
import kaita.stream_app_final.Activities.BottomSheet.BottomSheetDialogContainer
import kaita.stream_app_final.Adapteres.*
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.alertDialog
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.AppConstants.Constants.loaded
import kaita.stream_app_final.AppConstants.Constants.selected_id
import kaita.stream_app_final.Extensions.*
import kaita.stream_app_final.Fragments.General.HomeFragment
import kaita.stream_app_final.Fragments.General.NotificationsFragment
import kaita.stream_app_final.Fragments.General.ProfileFragment
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), BottomSheetDialogContainer.BottomSheetListener {

    private lateinit var obj_adapter: CustomAdapter
    val mAth = FirebaseAuth.getInstance()
    private lateinit var listener: SlideDateTimeListener
    private lateinit var alert: AlertDialog.Builder
    private lateinit var dateTimeDialogFragment: SwitchDateTimeDialogFragment
    private lateinit var ualert: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //push_A_B_C_D_Alphabets_to_Database()
        alert = AlertDialog.Builder(this)
        alertDialog = android.app.AlertDialog.Builder(this).create()
        is_user_logged_In()
        handle_One_Signal_Tags()
        // Initialize
        dateTimeDialogFragment =
            SwitchDateTimeDialogFragment.newInstance("Select A Date and Time", "Ok", "Dismiss")
        // Assign values
        dateTimeDialogFragment.startAtCalendarView()
        dateTimeDialogFragment.set24HoursMode(true)
        dateTimeDialogFragment.minimumDateTime = GregorianCalendar(2015, Calendar.JANUARY, 1).time
        dateTimeDialogFragment.maximumDateTime = GregorianCalendar(2025, Calendar.DECEMBER, 31).time
        dateTimeDialogFragment.setDefaultDateTime(
                GregorianCalendar(
                        2017,
                        Calendar.MARCH,
                        4,
                        15,
                        20
                ).time
        )
        // Define new day and month format
        try {
            dateTimeDialogFragment.simpleDateMonthAndDayFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.getDefault())
        } catch (e: SimpleDateMonthAndDayFormatException) {
            Log.e("DATE ERROR", e.message)
        }

        // Set listener
        dateTimeDialogFragment.setOnButtonClickListener(object : OnButtonClickListener {
            override fun onPositiveButtonClick(date: Date?) {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
                val str1: String = date.toString()
                val selectedDate = formatter.parse(str1)
                showAlertDialog(selectedDate.toString())
            }

            override fun onNegativeButtonClick(date: Date?) {
            }
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
        }
        setupBottomNavigationView()
        initall()
        configureToolbar()

        val uri: Uri? = intent.data
        if (uri != null) {
            try {
                val path: String = uri.toString()
                val ident = uri.getQueryParameter("id")

                var theid = path.substring(path.lastIndexOf("=") + 1)

                FirebaseChecker().load_selected_Streamer_Stream(theid) {
                    if (it.exists() && it.hasChildren()) {
                        selected_id = theid

                        val bottomSheet = BottomSheetDialogContainer()
                        val metrics = DisplayMetrics()

                        val bundle = Bundle()
                        bundle.putString("key", selected_id)
                        bottomSheet.arguments = bundle

                        CoroutineScope(Dispatchers.IO).launch {
                            windowManager?.defaultDisplay?.getMetrics(metrics)
                            if (loaded == false) {
                                bottomSheet.show(supportFragmentManager, selected_id)
                                loaded = true
                            }
                        }

                    } else {
                        showAlertDialog("This Stream is either closed or is pending")
                    }
                }

            } catch (e: Exception) {
                showAlertDialog("This Stream is either closed or is pending.\n${e.message}")
            }

        } else {
            /*if (FirebaseAuth.getInstance().currentUser != null) {
                check_if_user_has_agreed_to_the_Terms_Of_Service()
            }*/
        }
    }

    private fun check_if_user_has_agreed_to_the_Terms_Of_Service() {
        FirebaseChecker().load_All {
            if (it.exists()) {
                if (it.child("agreed").exists()) {
                } else {
                    show_Agree_To_Terms_Of_Service_Dialog()
                }
            }
        }
    }

    private fun show_Agree_To_Terms_Of_Service_Dialog() {

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        val agree_HashMap = HashMap<String, String>()
        agree_HashMap["agreed"] = "Agreed"
        agree_HashMap["agreed_Date"] = currentDate.toString()

        alert.setTitle("Terms Of Service")
        alert.setCancelable(false)
        alert.setMessage("To continue using Stream, Accept Its Terms And Services")
        alert.setIcon(R.drawable.mainicon)
        alert.setPositiveButton("I Agree",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.dismiss()
                                    FirebaseDatabase.getInstance().reference.child("users").child(firebaseAuth.currentUser.uid).child("agreed").push().setValue(agree_HashMap).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            makeLongToast("Successful")
                                        } else {
                                            makeLongToast("Error: ${it.exception.toString()}")
                                        }
                                    }
                                })
        alert.setNegativeButton("Terms",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    load_The_Url()
                                })
        alert.show()
    }

    private fun load_The_Url() {
        FirebaseDatabase.getInstance().reference.child("credentials").child("termsofservice").addListenerForSingleValueEvent(object :
                                                                                                                                     ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                makeLongToast("Error: ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val url = snapshot.value.toString()
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                } else {
                    makeLongToast("Url Credential Missing")
                }
            }
        })
    }

    private fun handle_One_Signal_Tags() {
        //The tagsAvailable callback does not return on the Main(UI) Thread, so be aware when modifying UI in this method.
        OneSignal.getTags {
            if (it != null) {
                runOnUiThread {
                    val ref = FirebaseDatabase.getInstance().reference.child("removetags")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            makeLongToast("Error: ${error.message}")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (value in snapshot.children) {
                                    val thetag = value.child("tag").value.toString()
                                    for (value in it.keys()) {
                                        val signal_Tag = value.toString()
                                        if (signal_Tag.equals(thetag)) {
                                            OneSignal.deleteTag(signal_Tag);
                                        }
                                    }
                                }
                            } else {
                                //makeLongToast("Error!, missing info.")
                            }
                        }
                    })
                }
            }
        }
    }

    private fun push_A_B_C_D_Alphabets_to_Database() {
        var c: Char
        c = 'a'
        while (c <= 'z') {
            val alphabet = HashMap<String, String>()
            alphabet["name"] = c.toString()
            FirebaseDatabase.getInstance().reference.child("alphabets").push().setValue(alphabet)
            ++c
        }
    }

    private fun initall() {
        is_user_logged_In()
    }

    private fun is_user_logged_In() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            goToActivity(this, SignUpActivity::class.java)
        } else {
            FirebaseDatabase.getInstance().getReference().child("users")
                .child(firebaseAuth.currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast(error.message.toString())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("admin").exists()) {
                            OneSignal.sendTag("admin", "admin")
                        }
                    }
                })
        }
    }

    /*Set UP Bottom Navigation*/
    private fun setupBottomNavigationView() {
        bottom_navigation.setOnNavigationItemSelectedListener(navListener);
    }

    private fun configureToolbar() {
        setSupportActionBar(toolbar_main)
        val actionbar: ActionBar? = supportActionBar
    }

    /*DOTTED MENU*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.dottedmenu, menu)

        val searchItem = menu?.findItem(R.id.search)
        val fragmentcheck = supportFragmentManager.findFragmentById(R.id.home)

        val searchView: SearchView = searchItem!!.actionView as SearchView
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val fragment: HomeFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as HomeFragment
                fragment.callAboutUsActivity(newText.toString())
                return false
            }
        })
        return true
    }

    /*DOTTED MENU ON CLICK LISTENER*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.create_Stream) {
            checkif_Everything_Is_Set_By_The_User()
        } else if (item.itemId == R.id.search) {
            //goToActivity_Unfinished(this, SearchActivity::class.java)
        }
        return true
    }

    private fun checkif_Everything_Is_Set_By_The_User() {
        FirebaseChecker().load_All {

            val name = it.child("name").value.toString()
            val email = it.child("email").value.toString()
            val mobile = it.child("mobileNumber").value.toString()
            val dpurl = it.child("dpurl").value.toString()
            val idnumber = it.child("idnumber").value.toString()

            if (name == "" || email == "" || mobile == "" || dpurl == "" || idnumber == "") {
                showAlertDialog("Complete your profile to Continue")
            } else if (name == null || email == null || mobile == null || dpurl == null || idnumber == null) {
                showAlertDialog("Complete your profile to Continue")
            } else if (name == "None" || email == "None" || mobile == "None" || dpurl == "None" || idnumber == "None") {
                showAlertDialog("Complete your profile to Continue")
            } else {
                goToActivity_Unfinished(this, PostActivity::class.java)
            }
        }
    }

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.home -> selectedFragment = HomeFragment()
            R.id.profile -> selectedFragment = ProfileFragment()
            R.id.notifications -> selectedFragment = NotificationsFragment()
        }
        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment).commit()
        } else {
            Log.d("Drawer Activity", "Error in creating Fragment")
        }
        true
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, "ProfileFragment", ProfileFragment());

    }

    override fun onStart() {
        super.onStart()
        check_if_user_has_agreed_to_the_Terms_Of_Service()
    }

    override fun onButtonClicked(text: String?) {
        //showAlertDialog("CLicked")
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
            .setTitle("Stream")
            .setCancelable(false)
            .setMessage("Are you sure you want to exit")
            .setIcon(R.drawable.mainicon)
            .setPositiveButton("Exit", DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finish()
            })
            .setNegativeButton("Dismis", DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
            .show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PAYPAL_REQUEST_CODE) {
            // If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                // Getting the payment confirmation
                val confirm : PaymentConfirmation = data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                // if confirmation is not null
                if (confirm != null) {
                    try {
                        // Getting the payment details
                        val paymentDetails = confirm.toJSONObject().toString(4);
                        // on below line we are extracting json response and displaying it in a text view.
                        val payObj =  JSONObject(paymentDetails);
                        val payID = payObj.getJSONObject("response").getString("id");
                        val state = payObj.getJSONObject("response").getString("state");
                        makeLongToast("Payment " + state + "\n with payment id is " + payID)

                        finish_UP_Bet()

                    } catch (e: JSONException) {
                        // handling json exception on below line
                        makeLongToast(e.message.toString())
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // on below line we are checking the payment status.
                makeLongToast("Payment Cancelled")
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // on below line when the invalid paypal config is submitted.
                makeLongToast("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.")
            }
        }
    }

    private fun finish_UP_Bet() {
        if (Constants.thedatabaseReference != null) {
            Constants.thedatabaseReference!!.setValue(Constants.hashMap_Selected_Bet_By_Better)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        FirebaseDatabase.getInstance().getReference()
                            .child("bets").child(
                                    Constants.firebaseAuth.currentUser.uid
                            ).push().setValue(
                                    Constants.hashMap_Selected_Bet_By_Better
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    if (Constants.progressDialog.isShowing) {
                                        Constants.progressDialog.dismiss()
                                    }
                                    showAlertDialog("Your bet was placed")
                                    Constants.chosen_Answer = ""
                                    sendNotification(selected_id, "One better joined bet")
                                    Constants.hashMap_Selected_Bet_By_Better.clear()
                                    Constants.thedatabaseReference.setValue(null)
                                    Constants.thebetamount = ""
                                    val reference =
                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(Constants.firebaseAuth.currentUser.uid)
                                            .child("betPay")
                                    reference.removeValue()
                                    FirebaseDatabase.getInstance().reference.child("keys")
                                        .child(
                                                selected_id
                                        ).addListenerForSingleValueEvent(object :
                                                                                                                     ValueEventListener {
                                            override fun onCancelled(error: DatabaseError) {
                                                makeLongToast("Error: ${error.message}")
                                            }

                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    val actual_id =
                                                        snapshot.value.toString()
                                                    if (actual_id.equals(
                                                                Constants.firebaseAuth.currentUser.uid.toString()
                                                        )
                                                    ) {
                                                        FirebaseDatabase.getInstance().reference.child(
                                                                "streams"
                                                        ).child(selected_id).child(
                                                                "contribution"
                                                        ).setValue(Constants.thebetamount)
                                                    }
                                                } else {
                                                    makeLongToast("Error!, missing info.")
                                                }
                                            }
                                        })

                                    register_To_OneSignal()

                                } else {
                                    makeLongToast("An error occured")
                                }
                            }
                    } else {
                        showAlertDialog("Failed to place bet, ${it.exception.toString()}")
                        Constants.hashMap_Selected_Bet_By_Better.clear()
                        if (Constants.progressDialog.isShowing) {
                            Constants.progressDialog.dismiss()
                        }
                    }
                }
        } else {
            makeLongToast("Something went wrong")
        }
    }

    private fun register_To_OneSignal() {
        OneSignal.sendTag(selected_id, selected_id)
    }

}