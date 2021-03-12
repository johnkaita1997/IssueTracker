@file:Suppress("NAME_SHADOWING")

package smartherd.githubissuetracker.Fragments

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import kotlinx.android.synthetic.main.fragment_one_compose.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import smartherd.githubissuetracker.Adapteres.Comments
import smartherd.githubissuetracker.Adapteres.CustomAdapter
import smartherd.githubissuetracker.Adapteres.Model
import smartherd.githubissuetracker.R
import smartherd.hiltonsteelandcement.LoadgitQuery
import java.text.SimpleDateFormat
import java.util.*
import smartherd.githubissuetracker.AppConstants.Constants.hash as collect

class HomePanel : Fragment() {

    private lateinit var viewy: View
    var arrayList_details: ArrayList<Model> = ArrayList();
    private lateinit var obj_adapter: CustomAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewy = inflater.inflate(R.layout.fragment_one_compose, container, false)

        obj_adapter = CustomAdapter(viewy.context, arrayList_details)
        val c = Calendar.getInstance()
        val daaaay = c.get(Calendar.DAY_OF_MONTH)
        val date = Date()
        var simpleDateFormat = SimpleDateFormat("EEEE")
        var day = simpleDateFormat.format(date).toUpperCase()
        simpleDateFormat = SimpleDateFormat("MMMM")
        var month = simpleDateFormat.format(date).toUpperCase()
        viewy.today.setText("Today $daaaay, $day, $month")
        viewy.recycler1.adapter = obj_adapter

        CoroutineScope(IO).launch {
            initall()
        }

        viewy.search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.length != 0) {
                    val tosearch = s
                    CoroutineScope(IO).launch {
                        makeSearch(tosearch)
                    }
                } else {
                    CoroutineScope(IO).launch {
                        initall()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        viewy.date.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                viewy.context,
                DatePickerDialog.OnDateSetListener { _, theyear, monthOfYear, dayOfMonth ->
                    var themonth = (monthOfYear + 1).toString()
                    if (themonth.toInt() < 10) {
                        themonth = "0$themonth"
                    }

                    var theday = (dayOfMonth).toString()
                    if (theday.toInt() < 10) {
                        theday = "0$theday"
                    }

                    val confirm_Date = "$theyear-${themonth}-$theday"
                    CoroutineScope(IO).launch {
                        check_with_date(confirm_Date)
                    }
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        viewy.reload.setOnClickListener {
            CoroutineScope(IO).launch {
                initall()
            }
        }

        return viewy
    }

    private suspend fun check_with_date(confirmDate: String) {
        val token = "119101c80b606678bbd73754004d1d6a005ced1e"
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

                collect[index] = comments

            }
        }
        activity?.runOnUiThread(Runnable {
            obj_adapter.notifyDataSetChanged()
            viewy.recycler1.requestLayout()

        })
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

        activity?.runOnUiThread(Runnable {
            obj_adapter.notifyDataSetChanged()
            viewy.recycler1.requestLayout()
        })
    }


    private suspend fun initall() {
        arrayList_details.clear()
        val token = "119101c80b606678bbd73754004d1d6a005ced1e"
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

            collect[index] = comments

        }

        activity?.runOnUiThread(Runnable {
            obj_adapter.notifyDataSetChanged()
            viewy.recycler1.requestLayout()
        })
    }
}



