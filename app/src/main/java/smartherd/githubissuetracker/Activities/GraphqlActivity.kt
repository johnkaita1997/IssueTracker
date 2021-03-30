package smartherd.githubissuetracker.Activities

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import kotlinx.android.synthetic.main.activity_graphql.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import smartherd.githubissuetracker.Adapteres.CustomAdapter
import smartherd.githubissuetracker.Adapteres.Model
import smartherd.githubissuetracker.AppConstants.Constants
import smartherd.githubissuetracker.R
import smartherd.hiltonsteelandcement.LoadgitQuery
import java.util.*
import smartherd.githubissuetracker.Adapteres.date_Converter as dateConverter

class GraphqlActivity : AppCompatActivity() {

    var arrayList_details: ArrayList<Model> = ArrayList();
    private lateinit var obj_adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)

        //to change title of activity
        val actionBar = supportActionBar
        actionBar!!.title = "Github Issues - Flutter"

        //Change action bar color
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B22222")));

        try {
            obj_adapter = CustomAdapter(this, arrayList_details)
            recycler1.adapter = obj_adapter

            //Change the date
            today.setText(dateConverter())

            CoroutineScope(Dispatchers.IO).launch {
                initall()
            }

            search.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (s.length != 0) {
                        val tosearch = s
                        CoroutineScope(Dispatchers.IO).launch {
                            makeSearch(tosearch)
                        }
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            initall()
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }
            })

            thedate.setOnClickListener {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, theyear, monthOfYear, dayOfMonth ->
                        var themonth = (monthOfYear + 1).toString()
                        if (themonth.toInt() < 10) {
                            themonth = "0$themonth"
                        }

                        var theday = (dayOfMonth).toString()
                        if (theday.toInt() < 10) {
                            theday = "0$theday"
                        }

                        val confirm_Date = "$theyear-${themonth}-$theday"
                        CoroutineScope(Dispatchers.IO).launch {
                            check_with_date(confirm_Date)
                        }
                    },
                    year,
                    month,
                    day
                )
                dpd.show()
            }

            reload.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    initall()
                }
            }
        } catch (e: Exception) {
            Log.d("Kaita-Graphql-Error", "check_with_date: ${e.message}")
        }

    }

    private suspend fun check_with_date(confirmDate: String) {
        try {
            //val token = "119101c80b606678bbd73754004d1d6a005ced1e"
            val token = "2d3825c85449fcc45777d7d54eeec069d3c0f9b3"
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val original: Request = chain.request()
                    val builder: Request.Builder = original.newBuilder().method(original.method(), original.body())
                    builder.header("Authorization", "bearer $token")
                    chain.proceed(builder.build())
                }
                .build()
            val apolloClient: ApolloClient = ApolloClient.builder()
                .serverUrl("https://api.github.com/graphql")
                .okHttpClient(okHttpClient)
                .build()

            val results = apolloClient.query(LoadgitQuery()).await()
            val theget = results.data?.organization?.repository?.issues?.nodes

            arrayList_details.clear()

            theget?.forEachIndexed { index, value ->
                val createdon = value?.createdAt
                val comments = value?.comments?.nodes
                val number = value?.number
                val title = value?.title
                val username = value?.author?.login
                val original_date = createdon as String
                val edited_date = original_date.take(10)

                if (edited_date == confirmDate) {
                    var model: Model = Model();
                    model.created_at = "Created On $edited_date"
                    model.comments = "Comments " + comments?.size.toString()
                    model.title = title + "  #$number"
                    model.developer = username
                    model.commentload = value.comments.nodes.toString()
                    model.state = value.state.toString()

                    arrayList_details.add(model)

                    Constants.hash[index] = comments

                }
            }
            this.runOnUiThread(Runnable {
                obj_adapter.notifyDataSetChanged()
                recycler1.requestLayout()

            })
        } catch (e: Exception) {
            Log.d("Kaita-Graphql-Error", "check_with_date: ${e.message}")
        }
    }

    private suspend fun makeSearch(tosearch: CharSequence) {

        var somearray: ArrayList<Model> = ArrayList();

        for (value in arrayList_details) {
            if (value.title!!.contains(tosearch, ignoreCase = true)) {
                somearray.add(value)
            }
        }

        arrayList_details.clear()

        for (value in somearray) {
            arrayList_details.add(value)
        }

        this.runOnUiThread(Runnable {
            obj_adapter.notifyDataSetChanged()
            recycler1.requestLayout()
        })
    }


    private suspend fun initall() {

        arrayList_details.clear()

        //val token = "119101c80b606678bbd73754004d1d6a005ced1e"
        val token = "2d3825c85449fcc45777d7d54eeec069d3c0f9b3"
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain: Interceptor.Chain ->
                val original: Request = chain.request()
                val builder: Request.Builder =
                    original.newBuilder().method(original.method(), original.body())
                builder.header("Authorization", "bearer $token")
                chain.proceed(builder.build())
            }
            .build()
        val apolloClient: ApolloClient = ApolloClient.builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(okHttpClient)
            .build()

        val results = apolloClient.query(LoadgitQuery()).await()
        val theget = results.data?.organization?.repository?.issues?.nodes

        theget?.forEachIndexed { index, value ->

            val createdon = value?.createdAt
            val comments = value?.comments?.nodes
            val number = value?.number
            val title = value?.title
            val username = value?.author?.login

            val original_date = createdon as String
            val edited_date = original_date.take(10)

            var model: Model = Model();
            model.created_at = "Created On $edited_date"
            model.comments = "Comments " + comments?.size.toString()
            model.title = title + "  #$number"
            model.developer = username
            model.commentload = value.comments.nodes.toString()
            model.state = value.state.toString()

            arrayList_details.add(model)

            Constants.hash[index] = comments

        }

        this.runOnUiThread(Runnable {
            obj_adapter.notifyDataSetChanged()
            recycler1.requestLayout()
        })
    }
}



