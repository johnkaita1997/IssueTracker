package smartherd.kenyamessagesolution

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.android.gms.gcm.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_one_forgot_password.*
import smartherd.kenyamessagesolution.Activities.OneLoginActivity
import smartherd.kenyamessagesolution.Activities.OneMainActivity
import smartherd.kenyamessagesolution.Extensions.goToActivity
import smartherd.kenyamessagesolution.Extensions.makeLongToast

class OneForgotPassword : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var mAth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_forgot_password)

        initall()

    }

    private fun initall() {

        btn_back.setOnClickListener {
            goToActivity(this, OneLoginActivity::class.java)
        }

        btn_reset_password.setOnClickListener {
            mAth = FirebaseAuth.getInstance()
            email = edt_reset_email.text.toString().trim()
            validateemail()
        }
    }

    private fun validateemail() {
        if (edt_reset_email.length()
                .equals(0)
        ) makeLongToast("You have to provide an email address.")
        else sendpasswordresetEmail()
    }

    private fun sendpasswordresetEmail() {
        mAth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                makeLongToast("We have sent an email to reset your password")
                goToActivity(this, OneLoginActivity::class.java)
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    baseContext,
                    "An error occured. Try again!" + task.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBackPressed() {
        goToActivity(this, OneLoginActivity::class.java)
    }
}
