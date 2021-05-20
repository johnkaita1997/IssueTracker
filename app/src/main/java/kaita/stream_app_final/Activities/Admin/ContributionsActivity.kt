package kaita.stream_app_final.Activities.Admin

import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.Adapteres.Finish.Finished
import kaita.stream_app_final.Adapteres.Finish.FinishedViewHolder
import kaita.stream_app_final.Adapteres.Finish.MoneyItem
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_contributions.*


class ContributionsActivity : AppCompatActivity() {

    private lateinit var id: String
    private lateinit var title: String
    private lateinit var numberofbetters: String
    private lateinit var correctanswer: String
    private lateinit var cashtotal: String
    private lateinit var hostname: String
    private lateinit var hostmobile: String
    private lateinit var winners: String
    private lateinit var losers: String

    private lateinit var percentage_cut: String
    private lateinit var todivide: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contributions)
        initall()
    }

    private fun initall() {
        id = intent.getStringExtra("access")
        load_Initial_Information()
        initiate_Firebase_Recycler_View_Options()
        calculation_Listener()
        delete_Bet_Listener()
    }

    private fun delete_Bet_Listener() {
        end_deleteButton.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("money").child(id).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    makeLongToast("Operation was successful")
                    Constants.finishAdapter.notifyDataSetChanged()
                } else {
                    makeLongToast("Error: ${it.exception.toString()}")
                }
            }
        }
    }


    private fun calculation_Listener() {

        end_Calculations.setOnClickListener {

            val full_contribution = cashtotal.toInt()
            val kalist: MutableList<MoneyItem> = mutableListOf()

            FirebaseDatabase.getInstance().reference.child("credentials").addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            makeLongToast("Error: ${error.message}")
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists() && snapshot.hasChildren()) {

                                val totalcutPercent = snapshot.child("totalcutPercent").value.toString().toInt()
                                Log.d("Kwisha", "TotalCut: $totalcutPercent")

                                val ours = snapshot.child("ours").value.toString().toInt()
                                Log.d("Kwisha", "Ours: $ours")

                                val hoster = snapshot.child("hoster").value.toString().toInt()
                                Log.d("Kwisha", "Hoster: $hoster")
                                Log.d("Kwisha", "Full Contribution : $full_contribution")

                                val ourmoney = (totalcutPercent * full_contribution)/100
                                Log.d("Kwisha", "Our Money : $ourmoney")

                                val thestreamearning = (ours * ourmoney)/100
                                Log.d("Kwisha", "TheStreamEarning : $thestreamearning")

                                val thehosterearning = (hoster * ourmoney)/100
                                Log.d("Kwisha", "TheHostEarning  : $thehosterearning")

                                val tobeshared = full_contribution - ourmoney
                                Log.d("Kwisha", "Tobeshared  : $tobeshared")

                                //Load all the winners
                                val winnerRef    =  FirebaseDatabase.getInstance().reference.child("money").child(id).orderByChild("wonorlost").equalTo("Won")
                                winnerRef.addListenerForSingleValueEvent(object: ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {
                                        makeLongToast("Error: ${error.message}")
                                    }
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            //Get the total amount each contributed
                                            var total = 0
                                            for (value in snapshot.children) {
                                                val betteramount = value.child("betteramount").value.toString().toInt()
                                                total = total + betteramount
                                            }
                                            Log.d("Kwisha", "Totaliiiii  : $total")

                                            for (value in snapshot.children) {
                                                val bettername = value.child("bettername").value.toString()
                                                val bettermobile = value.child("bettermobile").value.toString()
                                                val bettermoney = value.child("betteramount").value.toString().toInt()
                                                val hisShare = (bettermoney * tobeshared)/total
                                                val money = MoneyItem(bettername, bettermobile, hisShare.toString())

                                                Log.d("Kwisha", "mtalii  : $hisShare  $bettername, $bettermobile")
                                                kalist.add(money)

                                            }

                                            Log.d("Kwisha", "mtalii  : ${kalist.size}")

                                            percentage_cut = ourmoney.toString()
                                            todivide = tobeshared.toString()
                                            val host_Earning = thehosterearning.toString()
                                            val stream_Earning = thestreamearning.toString()
                                            var otherearning = ""
                                            for (value in kalist) {
                                                otherearning = otherearning  + "\nName: ${value.bettername}, Mob: ${value.bettermobile}, Amount: ${value.bettermoney}"
                                            }

                                            val full_String = "$title\nStream Earning: $stream_Earning\nHost Earning: $host_Earning$otherearning"
                                            Log.d("Kwisha", "Mwisho: $full_String")


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
                                            //Here they all lost, we take all the money
                                            makeLongToast("Error!, Missing winners info three info.")
                                        }
                                    }
                                })
                            } else {
                                makeLongToast("Admin Credentials not set up fully")
                            }
                        }
                    })
        }
    }


    fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            makeLongToast("Message was sent")
        } catch (ex: Exception) {
            makeLongToast(ex.message.toString())
        }
    }

    private fun load_Initial_Information() {
        val query = FirebaseDatabase.getInstance().reference.child("money").child(id).orderByChild("betteramount").limitToFirst(1)
        query.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (value in snapshot.children) {
                                title = value.child("title").value.toString()
                                end_Tittle.text = "$title"
                                correctanswer = value.child("correctanswer").value.toString().capitalize()
                                end_correctAnser.text = "Correct: $correctanswer"
                                hostname = value.child("hostname").value.toString()
                                end_hostName.text = "Host: $hostname"
                                hostmobile = value.child("hostmobile").value.toString()
                                end_hostMobile.text = "Host Mob: $hostmobile"
                            }
                            calculate_CashTotal_And_LostNumber_And_Winners_Number()
                        } else {
                            makeLongToast("Error!, missing one info.")
                        }
                    }
                })
    }

    private fun calculate_CashTotal_And_LostNumber_And_Winners_Number() {
        val query = FirebaseDatabase.getInstance().reference.child("money").child(id)
        query.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var thesum = 0
                            for (value in snapshot.children) {
                                val contributed = value.child("betteramount").value.toString()
                                thesum = thesum + contributed.toInt()
                            }

                            numberofbetters = snapshot.childrenCount.toString()
                            end_numberOfBetters.text = "All Betters: $numberofbetters"

                            cashtotal = thesum.toString()
                            end_CashTotal.text = "Total: $cashtotal"

                            calculate_NumberOf_Winners_And_Losers()
                        } else {
                            makeLongToast("Error!, missing two info.")
                        }
                    }
                })
    }

    private fun calculate_NumberOf_Winners_And_Losers() {
        val winnerRef    =  FirebaseDatabase.getInstance().reference.child("money").child(id).orderByChild("wonorlost").equalTo("Won")
        val losersRef    =  FirebaseDatabase.getInstance().reference.child("money").child(id).orderByChild("wonorlost").equalTo("Lost")
        winnerRef.addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            winners = snapshot.childrenCount.toString()
                            end_Winners.text = "Winners: $winners"
                        } else {
                            makeLongToast("Error!, missing three info.")
                        }
                    }
                })

        losersRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                makeLongToast("Error: ${error.message}")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    losers = snapshot.childrenCount.toString()
                    end_Losers.text = "Losers: $losers"
                } else {
                    makeLongToast("No losers for this bet")
                    losers = "0"
                    end_Losers.text = "Losers: 0"
                }
            }
        })
    }

    private fun initiate_Firebase_Recycler_View_Options() {
        finish_RecyclerView.setHasFixedSize(true)
        val mManager = LinearLayoutManager(this)
        finish_RecyclerView.setLayoutManager(mManager)

        val query = FirebaseDatabase.getInstance().reference
            .child("money")
            .child(id)

        //Initialize FirebasePagingOptions
        Constants.options = DatabasePagingOptions.Builder<Post>()
            .setLifecycleOwner(this)
            .setQuery(query, Constants.config, Post::class.java)
            .build()

        loadFirebaseAdapter()
    }

    private fun loadFirebaseAdapter() {
        val query = FirebaseDatabase.getInstance().reference.child("money").child(id);
        val options = FirebaseRecyclerOptions.Builder<Finished>()
            .setQuery(query, Finished::class.java)
            .build()

        Constants.finishAdapter = object : FirebaseRecyclerAdapter<Finished, FinishedViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): FinishedViewHolder {
                return FinishedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recycler_finish, parent, false))
            }
            override fun onBindViewHolder(holder: FinishedViewHolder, position: Int, finish: Finished) {
                val databaseRerence = getRef(position)
                holder.bind(finish, holder, this@ContributionsActivity)
            }
        }

        finish_RecyclerView.adapter = Constants.finishAdapter
    }

    override fun onStart() {
        super.onStart()
        Constants.finishAdapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        if (Constants.mAdapter != null) {
            Constants.finishAdapter.stopListening();
        }
    }
}