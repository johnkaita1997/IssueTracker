package kaita.stream_app_final.Activities.Normal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kaita.stream_app_final.Adapteres.*
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Fragments.CreateStream.Closed
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_post.*
import java.text.SimpleDateFormat
import java.util.*


class PostActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener,  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    //https://github.com/safaricom/android-daraja-sdk
    //https://github.com/safaricom/LNMOnlineAndroidSample
    //https://stackoverflow.com/questions/19109960/how-to-check-if-a-date-is-greater-than-another-in-java
    //https://gist.github.com/gmsharma3/1030254bc1f97aa958f5
    //https://gist.github.com/git-santosh/f982bc03b148fd77b1c56c4adb204931
    //https://www.quora.com/How-can-mobile-push-notifications-be-made-more-reliable
    //https://medium.com/firebase-developers/firestore-pagination-in-android-using-firebaseui-library-1d7fe1a75704 Firestore pagination
    //https://stackoverflow.com/questions/37711220/firebase-android-pagination Realtime pagination
    //https://github.com/orhanobut/dialogplus - Cool alert dialogs
    //https://medium.com/halcyon-mobile/implementing-googles-refreshed-modal-bottom-sheet-4e76cb5de65b Rounded borders to dialogs

    private lateinit var mPagerAdapter: SectionPagerAdapter
    private lateinit var fragment: Fragment
    private lateinit var fm: FragmentManager
    private lateinit var ft: FragmentTransaction
    private var lastday: String = ""
    private var cashday: String = ""
    private var clicked: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        initall()
    }

    private fun initall() {
        load_User_Image()
        fm = supportFragmentManager
        ft = fm.beginTransaction()
        ft.add(R.id.fragment_holder, Closed(), "Edit Profile")
        ft.commit()

        cash_Day_Button.setSafeOnClickListener {
            clicked = "cashday"
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "cashday")
        }

        end_voting_window_button.setSafeOnClickListener {
            clicked = "lastday"
            val datePicker: DialogFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "lastday")
        }

        /*closed_or_open.setSafeOnClickListener {
            val popup = PopupMenu(this, it)
            popup.setOnMenuItemClickListener(this@PostActivity)
            popup.inflate(R.menu.popup_menu)
            popup.show()
        }*/

    }

    private fun load_User_Image() {
        FirebaseChecker().load_All {
            val dpurl = it.child("dpurl").value.toString()
            Picasso
                .get()
                .load(dpurl)
                .placeholder(R.drawable.personb)
                .noFade()
                .into(profile_image)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item!!.itemId) {
            R.id.open_ended_menu -> {
               /* fragment = OpenEnded()
                ft = fm.beginTransaction()
                ft.replace(R.id.fragment_holder, fragment)
                ft.commit()*/
                true
            }
            R.id.closed_ended_menu -> {
                fragment = Closed()
                ft = fm.beginTransaction()
                ft.replace(R.id.fragment_holder, fragment)
                ft.commit()
                true
            }
            else -> false
        }
    }

    public fun get_Last_Day(): String {
        return lastday
    }

    public fun get_Cash_Day(): String {
        return cashday
    }

    private var itiswhat = ""
    private var themonth = 0
    private var theyear = 0
    private var thedayofthemonth = 0

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        themonth = month
        thedayofthemonth = dayOfMonth
        theyear = year

        if (clicked == "lastday") {
            timepickershow(month, dayOfMonth, year, "lastday")
        }else if (clicked == "cashday") {
            timepickershow(month, dayOfMonth, year, "cashday")
        }
    }

    private fun timepickershow(month: Int,dayOfMonth: Int,year: Int,lastorcashday: String ) {
        val timePicker: DialogFragment = TimePickerFragment(lastorcashday)
        timePicker.show(supportFragmentManager, lastorcashday)
        itiswhat = lastorcashday
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val simpleformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
        if (itiswhat == "lastday") {
            val c: Calendar = Calendar.getInstance()
            c.set(theyear, themonth, thedayofthemonth, hourOfDay, minute, 22)
            val chosenDateTime = simpleformat.format(c.time)
            lastday = chosenDateTime
            end_voting_window_button.setText("Last Day: $lastday - Tap to change")
            FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.currentUser.uid).child("lastday").setValue(lastday).addOnCompleteListener {
                if (it.isSuccessful) {
                } else {
                    makeLongToast("Date couldn't be saved, try again later")
                }
            }
        }else if (itiswhat == "cashday") {
            val c: Calendar = Calendar.getInstance()
            c.set(theyear, themonth, thedayofthemonth, hourOfDay, minute, 22)
            val chosenDateTime = simpleformat.format(c.time)
            cashday = chosenDateTime
            cash_Day_Button.setText("Results Day: $cashday - Tap to change")
            FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.currentUser.uid).child("cashday").setValue(cashday).addOnCompleteListener {
                if (it.isSuccessful) {
                } else {
                    makeLongToast("Date couldn't be saved, try again later")
                }
            }
        }
    }
}