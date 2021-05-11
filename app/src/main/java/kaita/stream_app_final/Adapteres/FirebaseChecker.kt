package kaita.stream_app_final.Adapteres

import android.content.Context
import com.google.firebase.database.*
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth

class FirebaseChecker {

    var context: Context? = null

    var homeRef_Streams: DatabaseReference = FirebaseDatabase.getInstance()
        .getReference()
        .child("streams")

    fun load_All(callback: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun load_Streamer_Current_Bet(callback: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference().child("streams").child(firebaseAuth.currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


     fun load_selected_Streamer_Stream( key: String, callback: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference().child("streams").child(key)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun load_selected_Streamer_Bets( key: String, callback: (DataSnapshot) -> Unit) {
        FirebaseDatabase.getInstance().getReference().child("streams").child(key).child("bets")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}