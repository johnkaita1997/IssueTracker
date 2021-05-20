package kaita.stream_app_final.Activities.Normal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_learn_how_it_works.*


class LearnHowItWorks : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_how_it_works)
        initall()
    }
    private fun initall() {

        listener_For_Terms_Of_Service()
        load_how_To_Bet_Text_From_The_Db()
        how_to_Bet_Video_Button_listener()
        listener_For_Go_To_Main_Activity()
    }

    private fun listener_For_Go_To_Main_Activity() {
        goToMainActivity.setOnClickListener {
            goToActivity(this, MainActivity::class.java)
        }
    }

    private fun how_to_Bet_Video_Button_listener() {
        watch_Video.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("credentials").child("howtobetvideo").addListenerForSingleValueEvent(object:
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    makeLongToast("Error: ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val videourl = snapshot.value.toString()
                        startActivity( Intent( Intent.ACTION_VIEW,Uri.parse(videourl)))
                    } else {
                        makeLongToast("Video Url Credential Missing")
                    }
                }
            })
        }

    }

    private fun load_how_To_Bet_Text_From_The_Db() {
        FirebaseDatabase.getInstance().reference.child("credentials").child("howtobettext").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                makeLongToast("Error: ${error.message}")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val howtobettext = snapshot.value.toString()
                    how_To_Bet_Text.setText(howtobettext)
                } else {
                    makeLongToast("Url Credential Missing")
                }
            }
        })
    }

    private fun listener_For_Terms_Of_Service() {
        terms_of_Service.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("credentials").child("termsofservice").addListenerForSingleValueEvent(object:
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
    }
}