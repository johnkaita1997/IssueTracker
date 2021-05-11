package kaita.stream_app_final.Activities.Normal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kaita.stream_app_final.AppConstants.Constants.googleSignInClient
import kaita.stream_app_final.R

class HomeActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_home)
        initall()
    }

    private fun initall() {

        initailize_Google()

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

}