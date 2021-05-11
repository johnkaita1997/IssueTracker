package kaita.stream_app_final.Activities.Authentication

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Normal.MainActivity
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var progressDialog: SpotsDialog
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mobileNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initall()
    }

    private fun initall() {

        progressDialog = SpotsDialog.Builder().setContext(this).build() as SpotsDialog
        initiate_Firebase_Callbacks()

        loginbutton.setSafeOnClickListener {

//            val mobileNumber = editTextMobile.text.toString().trim()
//
//            if (mobileNumber == "") {
//                makeLongToast("Enter your mobile")
//            } else {
//                if (!progressDialog.isShowing) {progressDialog.show()}
//                verifyThePhoneNumber(mobileNumber.replaceFirst("0", "+254"))
//            }

        }

        login_back_In_Email.setSafeOnClickListener {
            progressDialog = SpotsDialog.Builder().setContext(this).build() as SpotsDialog
            initiate_Firebase_Callbacks()

            val email = editTextEmail_sec.text.toString().trim()
            val password = thepassword_sec.text.toString().trim()

            if (email == "") {
                makeLongToast("Enter your email address")
            } else if(password == ""){
                makeLongToast("Enter your password")
            }
            else {
                if (!progressDialog.isShowing) {progressDialog.show()}
                signInUser(email, password)
            }
        }

        forgot_password.setSafeOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.forgot_pass_layout)
            val theemail = dialog.findViewById<EditText>(R.id.forgot_email_editText)
            val submitbutton: Button = dialog.findViewById(R.id.recover_Email_Button)
            submitbutton.setSafeOnClickListener {
                val added_Email = theemail.text.toString().trim()
                if (added_Email.isEmpty() || added_Email.isBlank() || added_Email == "") {
                    showAlertDialog("Enter the email address to proceed")
                } else {
                    if (!progressDialog.isShowing) {progressDialog.show()}
                    firebaseAuth.sendPasswordResetEmail(added_Email)
                        .addOnCompleteListener {
                            if (it.isSuccessful()) {
                                // do something when mail was sent successfully.
                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                if (dialog.isShowing) {dialog.dismiss()}
                                showAlertDialog("A password reset email has been sent to $added_Email, visit and come back for login")
                            } else {

                                if (progressDialog.isShowing) {progressDialog.dismiss()}
                                if (dialog.isShowing) {dialog.dismiss()}
                                showAlertDialog("Task Failed: ${it.exception.toString()}")
                            }
                        }
                }
            }
            dialog.show()
        }
    }

    private fun signInUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                    goToActivity(this, MainActivity::class.java)
                } else {
                    showAlertDialog("Failed. ${signIn.exception.toString()}")
                }
            }
    }

    private fun initiate_Firebase_Callbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("TAG", "onVerificationCompleted:$credential")
                val dialog = Dialog(this@LoginActivity)
                signInWithPhoneAuthCredential(credential, dialog)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    showAlertDialog("${e.message.toString()}")
                } else if (e is FirebaseTooManyRequestsException) {
                    showAlertDialog("${e.message.toString()}")
                }
                if (progressDialog.isShowing) {
                    progressDialog.dismiss()
                }
            }

            override fun onCodeSent(verificationId: String,token: PhoneAuthProvider.ForceResendingToken ) {
                val dialog = Dialog(this@LoginActivity)
                dialog.setContentView(R.layout.verify_popup)
                dialog.setCancelable(false)
                val etVerifyCode = dialog.findViewById<EditText>(R.id.etVerifyCode)
                val btnVerifyCode: Button = dialog.findViewById(R.id.btnVerifyOTP)
                btnVerifyCode.setSafeOnClickListener {
                    val verificationCode = etVerifyCode.text.toString()
                    if (verificationId.isEmpty()) {
                        showAlertDialog("Enter the verification code")
                    } else {
                        //create a credential
                        val credential =PhoneAuthProvider.getCredential(verificationId, verificationCode)
                        Log.d("Verification Completed", "onVerificationCompleted:$credential")
                        signInWithPhoneAuthCredential(credential, dialog)

                    }
                }
                dialog.show()
            }
        }
    }

    private fun verifyThePhoneNumber(mobileNumber: String) {
        Constants.firebaseAuth.setLanguageCode(Locale.getDefault().language)
        val options = PhoneAuthOptions.newBuilder(Constants.firebaseAuth)
            .setPhoneNumber(mobileNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, dialog: Dialog) {
        Constants.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (dialog.isShowing) {dialog.dismiss()}
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                    goToActivity(this, MainActivity::class.java)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showAlertDialog("Invalid Token")
                    }
                    if (dialog.isShowing) {dialog.dismiss()}
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                }
            }
    }
}