package smartherd.kenyamessagesolution.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast.*
import kotlinx.android.synthetic.main.activity_coroutine_jobs.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import smartherd.kenyamessagesolution.Extensions.makeLongToast
import smartherd.kenyamessagesolution.R

class CoroutineJobs : AppCompatActivity() {

    companion object {

        private val TAG: String = "AppDebug"
        private val PROGRESS_MAX = 100
        private val PROGRESS_START = 0
        private val JOB_TIME = 4000 // ms
    }

    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine_jobs)

        initall()

    }

    fun initall() {

        job_button.setOnClickListener {
            if (!::job.isInitialized) {
                initjob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }


    fun initjob() {
        job_button.setText("Start Job #1")
        updateJobCompleteTextView("")

        //Since it was a latinit we make sure we initialize that.
        job = Job()
        job.invokeOnCompletion {
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "Unknown cancellation error."
                }
                Log.e(TAG, "${job} was cancelled. Reason: ${msg}")
                makeLongToast(msg)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }

    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            Log.d(TAG, "${job} is already active. Cancelling...")
            resetjob()
        } else {
            job_button.setText("Cancel Job #1")
            CoroutineScope(IO + job).launch {
                Log.d(TAG, "coroutine ${this} is activated with job ${job}.")

                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete!")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.setText(text)
        }
    }

    private fun showToast(text: String) {
        GlobalScope.launch(Main) {
            makeText(this@CoroutineJobs, text, LENGTH_SHORT).show()
        }
    }

    fun resetjob() {
        if (job.isActive || job.isCompleted) {
            job.cancel(CancellationException("Resetting job"))
        }
        initjob()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
