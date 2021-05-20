package kaita.stream_app_final.Activities.Admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kaita.stream_app_final.Adapteres.Finish.MoneyItem
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_delete_return.*

class DeleteReturn : AppCompatActivity() {

    lateinit var returnList: MutableList<MoneyItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_return)
        initall()
    }

    private fun initall() {
        returnList = mutableListOf()
        return_Button.setOnClickListener {
            val id_Of_Stream = return_editText.text.toString().trim()
            if (id_Of_Stream == "") {
                makeLongToast("Enter the Stream Id first")
            } else {
                FirebaseChecker().homeRef_Streams.child(id_Of_Stream).child("bets").addListenerForSingleValueEvent(object:
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            Log.d("Kwisha", "Exists")

                            for (value in snapshot.children) {
                                val theamount = value.child("bettamount").value.toString()
                                val bettername = value.child("bettername").value.toString()
                                val bettermobile = value.child("bettermobile").value.toString()
                                val money = MoneyItem(bettername, bettermobile, theamount)
                                returnList.add(money)
                            }

                            Log.d("Kwisha", "Money Size ${returnList.size}")

                            val title = "Return Money"
                            var otherearning = ""
                            for (value in returnList) {
                                otherearning = otherearning  + "\nName: ${value.bettername}, Mob: ${value.bettermobile}, Amount: ${value.bettermoney}"
                            }
                            val full_String = "$title\n$otherearning"

                            makeLongToast("here")

                            FirebaseDatabase.getInstance().reference.child("credentials").child("paymentsmsmobile") .addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    makeLongToast("Error: ${error.message}")
                                }
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val number = snapshot.value.toString()
                                        sendSMS(number, full_String)
                                    } else {
                                        makeLongToast("Sms Mpesa Credential Phone number missing")
                                    }
                                }
                            })

                        } else {
                            makeLongToast("No bets on this stream")
                        }
                    }
                })
            }
        }

        return_deleteStream.setOnClickListener {
            val id_Of_Stream = return_editText.text.toString().trim()
            if (id_Of_Stream == "") {
                makeLongToast("Enter the Stream Id first")
            } else {
                FirebaseChecker().homeRef_Streams.child(id_Of_Stream).removeValue().addOnCompleteListener {
                            if (it.isSuccessful) {
                                makeLongToast("Operation was successful")
                                return_editText.setText("")
                            } else {
                                makeLongToast("Error: ${it.exception.toString()}")
                            }
                        }
            }
        }
    }

    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            makeLongToast("Message was sent")
            return_editText.setText("")
        } catch (ex: Exception) {
            makeLongToast(ex.message.toString())
        }
    }

}