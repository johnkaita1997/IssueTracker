package kaita.stream_app_final.Activities.Admin

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kaita.stream_app_final.Extensions.makeLongToast
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_money.*


class MoneyActivity : AppCompatActivity() {

    var your_array_list = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        initall()
    }

    private fun initall() {
        load_All_Keys()
    }

    private fun load_All_Keys() {

        val ref  = FirebaseDatabase.getInstance().reference.child("money")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        makeLongToast("Error: ${error.message}")
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists() && snapshot.hasChildren()) {
                            for (value in snapshot.children) {
                                val key = value.key
                                your_array_list.add(key.toString())
                            }
                            val arrayAdapter = ArrayAdapter( this@MoneyActivity,android.R.layout.simple_list_item_1,your_array_list)
                            money_ListView.adapter = arrayAdapter

                        } else {
                            makeLongToast("Money Information is unavailable")
                        }
                    }
                })

        money_ListView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val listItem: Any = money_ListView.getItemAtPosition(position)
            val thechosenId = listItem.toString()
            val intent = Intent(this, ContributionsActivity::class.java)
            intent.putExtra("access",  thechosenId)
            startActivity(intent)
        })
    }
}