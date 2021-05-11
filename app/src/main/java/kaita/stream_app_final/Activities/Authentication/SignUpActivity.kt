package kaita.stream_app_final.Activities.Authentication

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Normal.MainActivity
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.goToActivity_Unfinished
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.Extensions.showAlertDialog
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    //Companion object for keeping constants
    companion object {
        private const val RC_SIGN_IN = 125
    }

    private lateinit var progressDialog: SpotsDialog
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var name : String
    private lateinit var email : String
    private lateinit var mobileNumber : String 
    private lateinit var password : String
    private lateinit var googleSignInClient: GoogleSignInClient
    private var prefs: SharedPreferences? = null
    private lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_sign_up)
            initall()
        } catch (e: Exception) {
            showAlertDialog(e.message.toString())
        }
    }

    private fun initall() {

        is_User_Logged_In()
        initailize_Google()
        initailize_Facebook()
        prefs = getSharedPreferences("kaita.stream_app_final", MODE_PRIVATE);
        progressDialog = SpotsDialog.Builder().setContext(this).build() as SpotsDialog
        initiate_Firebase_Callbacks()

        createaccount.setSafeOnClickListener {
            name = editTextName.text.toString().trim()
            email = editTextEmail.text.toString().trim()
            mobileNumber = editTextMobile.text.toString().trim()
            password = thepassword.text.toString().trim()
            if (name == "") {
                makeLongToast("Enter your name")
            } else if (email == "") {
                makeLongToast("Enter your email")
            } else if (mobileNumber == "") {
                makeLongToast("Enter your mobile")
            } else if (password == "") {
                makeLongToast("Enter a password")
            } else {
                verifyThePhoneNumber(name, email, mobileNumber.replaceFirst("0", "254"), password)
                if (!progressDialog.isShowing) {
                    progressDialog.show()
                }
            }
        }

        already_have_an_account_Button.setSafeOnClickListener {
            goToActivity_Unfinished(this, LoginActivity::class.java)
        }

        google_button.setSafeOnClickListener {
            if (!progressDialog.isShowing) { progressDialog.show()}
            signIn()
        }

        facebook_Button.setSafeOnClickListener {
            if (!progressDialog.isShowing) { progressDialog.show()}
        }

        email_And_Pass_Button.setSafeOnClickListener {
            name = editTextName.text.toString().trim()
            email = editTextEmail.text.toString().trim()
            mobileNumber = editTextMobile.text.toString().trim()
            password = thepassword.text.toString().trim()
            if (name == "") {
                makeLongToast("Enter your name")
            } else if (email == "") {
                makeLongToast("Enter your email")
            } else if (mobileNumber == "") {
                makeLongToast("Enter your mobile")
            } else if (password == "") {
                makeLongToast("Enter a password")
            } else {
                email_Sign_In(name, email, mobileNumber, password)
                if (!progressDialog.isShowing) {
                    progressDialog.show()
                }
            }
        }
    }

    private fun initailize_Facebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        facebook_Button.setReadPermissions("email", "public_profile")
        facebook_Button.registerCallback(callbackManager, object :FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                if (progressDialog.isShowing) {progressDialog.dismiss()}                               
               showAlertDialog("You cancelled your login with Facebook")
            }
            override fun onError(error: FacebookException) {
                if (progressDialog.isShowing) {progressDialog.dismiss()}                               
                showAlertDialog("Facebook Login Error: ${error.message.toString()}")
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    var userid = user.uid
                    var name = user.displayName
                    var email = user.email
                    var mobileNumber = user.phoneNumber
                    var dpurl = user.photoUrl.toString()

                    if (mobileNumber == null || mobileNumber == "") {mobileNumber = "None"}
                    if (email == null || email == "") {email = "None"
                        if (name == null || name == "") {name = "None"}}
                    if (dpurl == null || dpurl == "") {dpurl = "None"}
                    password = "None"
                    
                    if (prefs!!.getBoolean(email, true)) {
                        add_User_To_The_Database(name, email, mobileNumber, password, dpurl)
                        prefs!!.edit().putBoolean(email, false).commit();
                    } else {
                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                        goToActivity(this, MainActivity::class.java)
                    }                               
                } else {
                    showAlertDialog("Facebook Authentication Failed: ${task.exception.toString()}")
                    if (progressDialog.isShowing) {progressDialog.dismiss()}                               
                }
            }
    }

    private fun signIn() {
        //Here the application prompts the user to sign in with the google account
        val signInIntent = googleSignInClient.signInIntent
        //Sends A Callback
        startActivityForResult(signInIntent,
            RC_SIGN_IN
        )
    }

    private fun initailize_Google() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        //Initialize the google sign in client
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun is_User_Logged_In() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            facebook_Button.text = "Login"
        } else {
            goToActivity(this, MainActivity::class.java)
        }
    }

    private fun email_Sign_In(name: String, email: String, mobileNumber: String, password: String) {
        /*create a user*/
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification(name, email, mobileNumber, password)
                    if (progressDialog.isShowing) {
                        progressDialog.setMessage("Verifying your email")
                    }
                } else {
                    showAlertDialog("Failed ${task.exception.toString()}")
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                }
            }
    }

    private fun sendEmailVerification(
        name: String,
        email: String,
        mobileNumber: String,
        password: String
    ) {
        firebaseAuth.currentUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    signInUser(name, email, mobileNumber, password)
                } else {
                    showAlertDialog(task.exception.toString())
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                }
            }
        }
    }

    private fun signInUser(name: String, email: String, mobileNumber: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signIn ->
                if (signIn.isSuccessful) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    val dpurl = "None"
                    add_User_To_The_Database(name, email, mobileNumber, password, dpurl)
                } else {
                    showAlertDialog("Failed. ${signIn.exception.toString()}")
                }
            }
    }

    private fun add_User_To_The_Database(name: String, email: String, mobileNumber: String, password: String, dpurl: String) {
        ///Add the values to the database firs.
        val userid = firebaseAuth.getCurrentUser().getUid()
        val userMap = HashMap<String, String>()
        userMap["name"] = name
        userMap["email"] = email
        userMap["password"] = password
        userMap["mobileNumber"] = mobileNumber
        userMap["identity"] = userid
        userMap["dpurl"] = dpurl

        val homeRef = FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .child(userid)
        val homeRef_Users = FirebaseDatabase.getInstance()
            .getReference()
            .child("users")
            .child(userid)

        homeRef_Users.setValue(userMap)
            .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                if (task.isSuccessful) {
                    makeLongToast("Account finished setting up")
                    goToActivity(this, MainActivity::class.java)
                    if (progressDialog.isShowing) {progressDialog.dismiss()}                               
                } else {
                    showAlertDialog("We created your account but couldn't finish setting up user data, you may need to this later")
                    goToActivity(this, MainActivity::class.java)
                    if (progressDialog.isShowing) {progressDialog.dismiss()}                               
                }
            })
    }

    private fun initiate_Firebase_Callbacks() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val dialog = Dialog(this@SignUpActivity)
                signInWithPhoneAuthCredential(credential, dialog, name, email, mobileNumber, password)
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

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                val dialog = Dialog(this@SignUpActivity)
                dialog.setContentView(R.layout.verify_popup)
                dialog.setCancelable(false)
                val etVerifyCode = dialog.findViewById<EditText>(R.id.etVerifyCode)
                val btnVerifyCode: Button = dialog.findViewById(R.id.btnVerifyOTP)
                btnVerifyCode.setSafeOnClickListener {
                    val verificationCode = etVerifyCode.text.toString().trim()
                    if (verificationId.isEmpty()) {
                        showAlertDialog("Enter the verification code")
                    } else {
                        //create a credential
                        val credential =
                            PhoneAuthProvider.getCredential(verificationId, verificationCode)
                        signInWithPhoneAuthCredential(credential, dialog, name, email, mobileNumber, password)
                    }
                }
                dialog.show()
            }
        }
    }

    private fun verifyThePhoneNumber(name: String, email: String, mobileNumber: String,password: String) {
        firebaseAuth.setLanguageCode(Locale.getDefault().language)
        //verify phone number
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(mobileNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, dialog: Dialog, name: String, email:String, mobileNumber: String, password: String) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (dialog.isShowing) {dialog.dismiss()}
                    if (progressDialog.isShowing) {
                        progressDialog.setMessage("Saving your info...")
                    }
                    val dpurl = "None"
                    add_User_To_The_Database(name, email, mobileNumber, password, dpurl)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        showAlertDialog("Invalid Token")
                    }
                    if (dialog.isShowing) {dialog.dismiss()}
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = firebaseAuth.currentUser
                        var userid = user.uid
                        var name = user.displayName
                        var email = user.email
                        var mobileNumber = user.phoneNumber
                        var dpurl = user.photoUrl.toString()

                        if (mobileNumber == null || mobileNumber == "") {mobileNumber = "None"}
                        if (email == null || email == "") {email = "None"
                        if (name == null || name == "") {name = "None"}}
                        if (dpurl == null || dpurl == "") {dpurl = "None"}

                        password = "None"
                        if (prefs!!.getBoolean(email, true)) {
                            add_User_To_The_Database(name, email, mobileNumber, password, dpurl)
                            prefs!!.edit().putBoolean(email, false).commit();
                        } else {
                            if (progressDialog.isShowing) {progressDialog.dismiss()}
                            goToActivity(this, MainActivity::class.java)
                        }
                    } else {
                        if (progressDialog.isShowing) {progressDialog.dismiss()}
                        showAlertDialog("Sign in with google failed\nReason: ${task.exception.toString()}")
                    }
                }.addOnFailureListener {
                    if (progressDialog.isShowing) {progressDialog.dismiss()}
                    showAlertDialog("Failed: ${it.message.toString()}")
                }
        } catch (e: Exception) {
            showAlertDialog(e.message.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                   showAlertDialog("Google-Sign-In Failed ${e.message.toString()}")
                }
            } else {
                if (progressDialog.isShowing) {progressDialog.dismiss()}
                showAlertDialog("Task Failed:" + exception?.localizedMessage.toString())
            }
        }else{
            //Call back Facebook App
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}