package smartherd.kenyamessagesolution.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_hobbies.*
import smartherd.kenyamessagesolution.Adapteres.HobbiesAdapter
import smartherd.kenyamessagesolution.R
import smartherd.kenyamessagesolution.Modals.Supplier

class HobbiesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobbies)

        initall()
    }

    fun initall() {

        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        //Now attach layoutmanager to the recyclerview
        recyclerView.layoutManager = layoutManager

        //Now initialize the hobbies adapter created in the previous video
        val adapter = HobbiesAdapter(this, Supplier.hobbies)
        recyclerView.adapter = adapter
    }
}
