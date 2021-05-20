package kaita.stream_app_final.Activities.Normal

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.appcompat.app.AppCompatActivity
import com.quinny898.library.persistentsearch.SearchBox
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initall()
    }

    private fun initall() {
        searchbox.enableVoiceRecognition(this);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            searchbox.populateEditText(matches.toString())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}