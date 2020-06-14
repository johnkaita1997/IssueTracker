package smartherd.kenyamessagesolution.Activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hotchemi.android.rate.AppRate
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_one_compose.*
import smartherd.kenyamessagesolution.Extensions.*
import smartherd.kenyamessagesolution.OneForgotPassword
import smartherd.kenyamessagesolution.R

class OneLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneemail: String
    private lateinit var onepassword: String
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initall()
    }

    fun initall() {

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //When user has no account
        noaccount.setOnClickListener {
            goToActivity(this, OneSignupActivity::class.java)
        }

        login.setOnClickListener {
            oneemail = email.text.toString().trim()
            onepassword = password.text.toString().trim()
            checkfornullvalues()
        }
        forgot.setOnClickListener {
            goToActivity(this, OneForgotPassword::class.java)
        }
    }

    private fun checkfornullvalues() {
        if (oneemail.length.equals(0)) Toast.makeText(
            this,
            "You cannot leave email field blank",
            Toast.LENGTH_LONG
        ).show()
        else if (onepassword.length.equals(0)) makeLongToast("You will need a password to continue")
        else {
            showredirect()
            logtheuserin()
        }

        oneemail = email.text.toString().trim()
        onepassword = password.text.toString().trim()


    }

    private fun logtheuserin() {
        auth.signInWithEmailAndPassword(oneemail, onepassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    dismissredirect()

                    //Check if user has paid
                    var db = FirebaseFirestore.getInstance()
                    db.collection(auth.currentUser!!.email.toString()).document("Payment")
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                goToActivity(this, OneMainActivity::class.java)
                            } else {
                                alert = AlertDialog.Builder(this)
                                    .setTitle("KMS")
                                    .setCancelable(false)
                                    .setMessage("This account is not associated with any payment.To use KMS, contact +254 729 522550")
                                    .setIcon(R.drawable.mainicon)
                                    .setPositiveButton("",
                                        DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                                    .setNegativeButton("EXIT",
                                        DialogInterface.OnClickListener { dialog, _ ->
                                            System.exit(0)
                                            dialog.dismiss()
                                        })
                                    .show()
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed. Try again!" + task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    dismissredirect()
                }
            }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun rateApp() {
        AppRate.with(this)
            .setInstallDays(3)
            .setLaunchTimes(2)
            .setRemindInterval(5)
            .monitor()
        AppRate.showRateDialogIfMeetsConditions(this)
    }
}
