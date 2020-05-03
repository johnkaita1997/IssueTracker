package smartherd.com.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import smartherd.com.AppConstants.Constants
import smartherd.com.R
import smartherd.com.Extensions.makeLongToast
import smartherd.com.Modals.Hobby
import smartherd.com.Modals.Supplier

class MainActivity : AppCompatActivity() {

    companion object{
        val TAG: String = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            Log.i(TAG, "The button was already cliced")
            makeLongToast("Clicked")
        }

        submit.setOnClickListener {
            //Extract the user message
            val message = editText.text.toString().trim()
            if (message.length.equals(0)) Toast.makeText(
                this,
                "You have to enter a message",
                Toast.LENGTH_LONG
            ).show()
            else
            //Get the text and move to the next activity]
                goToSecondActivity(message)

            Hobby("mink")
        }

        shareToOtherApps.setOnClickListener {
            //Go to a different application
            val intent = Intent()
            //Define what we will be sharing
            val message = editText.text.toString().trim()
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share to..."))
            Supplier.hobbies.add("Having fun")
        }

        demo.setOnClickListener {
            val intent = Intent(this, HobbiesActivity::class.java)
            startActivity(intent)
        }

    }



    fun goToSecondActivity(message: String) {
        val intent: Intent = Intent(this, Main2Activity::class.java)
        intent.putExtra(Constants.USER_MESG_KEY, message)
        startActivity(intent)
    }
}