package smartherd.githubissuetracker.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smartherd.githubissuetracker.Adapteres.CustomAdapter
import smartherd.githubissuetracker.Adapteres.Model
import smartherd.githubissuetracker.R
import java.util.*
import smartherd.githubissuetracker.AppConstants.Constants.hash as collect


class Comments : AppCompatActivity() {

    var arrayList_details: ArrayList<Model> = ArrayList();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        //stuff that updates ui
        val obj_adapter: CustomAdapter
        obj_adapter = CustomAdapter(this, arrayList_details)

        CoroutineScope(Dispatchers.IO).launch {
            initall()
        }
        this.recycler1.adapter = obj_adapter
    }


    private suspend fun initall() {
        val intent = intent
        val mlist = intent?.getStringExtra("index")
        val position = mlist?.toInt()


        collect[position]?.forEach { value ->

            var thecomment = value?.body
            var theauthor = value?.author
            var dateCreated = value?.createdAt

            val original_date = dateCreated as String
            val edited_date = original_date.take(10)

            var model: Model = Model()
            model.created_at = "Created On $edited_date"
            model.comments = "None"
            model.title = thecomment
            model.developer = theauthor?.login

            arrayList_details.add(model)

        }

    }
}