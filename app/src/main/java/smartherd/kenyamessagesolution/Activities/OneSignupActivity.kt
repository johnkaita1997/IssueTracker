package smartherd.kenyamessagesolution.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_one_signup.*
import smartherd.kenyamessagesolution.Extensions.*
import smartherd.kenyamessagesolution.R

class OneSignupActivity : AppCompatActivity() {

    private lateinit var mAth: FirebaseAuth
    private lateinit var onecompanyName: String
    private lateinit var companyEmail: String
    private lateinit var companyPassword: String
    private lateinit var companyMobile: String
    private lateinit var db: FirebaseFirestore
    private lateinit var databaseHome : DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_signup)

        initall()
    }

    fun initall() {
        mAth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        createaccount.setOnClickListener {
            companyEmail = email.text.toString().trim()
            companyMobile = mobile.text.toString().trim()
            companyPassword = password.text.toString().trim()
            onecompanyName = companyname.text.toString().trim()
            checkfornullvalues()
        }
    }

    private fun checkfornullvalues() {
        if (email.text.length.equals(0)) Toast.makeText(
            this,
            "You cannot leave email field blank",
            Toast.LENGTH_LONG
        ).show()
        else if (password.text.length.equals(0)) makeLongToast("You will need a password to continue")
        else if (mobile.text.length.equals(0)) makeLongToast("You will need a password to continue")
        else if (companyname.text.length.equals(0)) makeLongToast("You will need a password to continue")
        else if (password.text.length < 6) makeLongToast("Password has to be 6 characters or more")
        else {
            showredirect()
            signuptheuser()
        }
    }

    private fun signuptheuser() {
        mAth.createUserWithEmailAndPassword(companyEmail, companyPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    databaseHome = db.collection(mAth.currentUser!!.email.toString()).document("Credentials")
                    storethevaluestofirebase()
                    //Save user and email.
                    // Sign in success, update UI with the signed-in user's information
                    makeLongToast("Sign up was successful")
                    dismissredirect()
                    //Check if user has paid
                    var db = FirebaseFirestore.getInstance()
                    db.collection(mAth.currentUser!!.email.toString()).document("Payment")
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                goToActivity(this, OneMainActivity::class.java)
                            } else {
                                alert = AlertDialog.Builder(this)
                                    .setTitle("KMS")
                                    .setCancelable(false)
                                    .setMessage("Thank you for signing up.This account is not associated with any payment.To use KMS, contact +254 729 522550")
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
                    dismissredirect()
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.Please try again now" + task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun storethevaluestofirebase() {

        val note: MutableMap<String, Any> = HashMap()
        note["Tittle"] = onecompanyName
        note["Email"] = companyEmail
        note["Mobile"] = companyMobile
        note["password"] = companyPassword

        databaseHome.set(note)
            .addOnSuccessListener {
            }.addOnFailureListener {
                makeLongToast("Error saving company credentials, you will need to update these later.")
            }
    }

    override fun onBackPressed() {
        goToActivity(this, OneLoginActivity::class.java)
    }
}
