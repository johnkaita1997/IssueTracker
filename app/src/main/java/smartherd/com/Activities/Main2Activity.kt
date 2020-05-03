package smartherd.com.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import smartherd.com.AppConstants.Constants
import smartherd.com.Extensions.makeLongToast
import smartherd.com.R

class Main2Activity : AppCompatActivity() {

    companion object {
        //val result1 = getResult1FromApi() // wait until job is done
        val JOB_TIMEOUT = 1900L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        initall()
    }

    fun initall() {

        loadIntentBundles()

        button.setOnClickListener {

            //IO, Main and Default
            CoroutineScope(IO).launch {
                methods.fakeApiRequest()
            }
        }

        //This is an onclicklistener for the second button
        button2.setOnClickListener {
            //IO, Main and Default
            CoroutineScope(IO).launch {
                secondsMethod.fakeApiREquest1()
            }
        }

        //For the textView to receive second result
        textView2.setOnClickListener {

        }
    }


    private fun loadIntentBundles() {
        val bundle: Bundle? = intent.extras

        //First check if bundle is not null first
        //The below will be done only if bundle is not null
        bundle?.let {
            val message = bundle.getString(Constants.USER_MESG_KEY)
            //Set the value to the textview
            textView.text = message
        }
    }


    //This object does the second methods.
    object secondsMethod : AppCompatActivity() {

        suspend fun fakeApiREquest1() {
            //We need to treat Coroutines as jobs
            /*withContext(IO) {
                val job = launch {
                    val result1 = methods.getResult1FromApi()
                    methods.setTextOnMainThread("Got $result1")

                    val result2 = methods.getResult2FromApi()
                    methods.setTextOnMainThread("Got $result2")
                }
            }*/

            //Now instead of using launch i can call Job Timeout
            //We need to treat Coroutines as jobs
            withContext(IO) {
                val job = withTimeoutOrNull(JOB_TIMEOUT) {
                    val result1 = methods.getResult1FromApi()
                    methods.setTextOnMainThread("Got $result1")

                    val result2 = methods.getResult2FromApi()
                    methods.setTextOnMainThread("Got $result2")
                }//Will wait till completion
                if (job == null) {
                    //It will return a null value if it did not time out
                    val cancel_Message = "Cancelling job....job took longet than $JOB_TIMEOUT MS"
                    makeLongToast(cancel_Message)
                }
            }
        }
    }


    //This object does everything for the first API
    object methods : AppCompatActivity() {

        suspend fun getResult1FromApi(): String {
            logThread("getResult1FromApi")
            delay(1000) // Does not block thread. Just suspends the coroutine inside the thread
            return "Result #1"
        }

        private fun setNewText(input: String) {
            val newText = text.text.toString() + "\n$input"
            text.text = newText
        }

        suspend fun setTextOnMainThread(input: String) {
            withContext(Main) {
                setNewText(input)
            }
        }

        private fun logThread(methodName: String) {
            println("debug: ${methodName}: ${Thread.currentThread().name}")
        }

        suspend fun fakeApiRequest() {
            logThread("fakeApiRequest")

            val result1 = getResult1FromApi() // wait until job is done

            if (result1.equals("Result #1")) {

                setTextOnMainThread("Got $result1")

                val result2 = getResult2FromApi() // wait until job is done

                if (result2.equals("Result #2")) {
                    setTextOnMainThread("Got $result2")
                } else {
                    setTextOnMainThread("Couldn't get Result #2")
                }
            } else {
                setTextOnMainThread("Couldn't get Result #1")
            }
        }

        suspend fun getResult2FromApi(): String {
            logThread("getResult2FromApi")
            delay(1000)
            return "Result #2"
        }
    }
}
