/*
package kaita.stream_app_final.Activities

import android.widget.ListView
import androidx.test.filters.SmallTest
import androidx.test.rule.ActivityTestRule
import kaita.stream_app_final.Activities.Normal.MainActivity
import kaita.stream_app_final.R
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

public class MainActivityTest_Instrumented {

    @get:Rule
    var rule = ActivityTestRule( MainActivity::class.java)

    @SmallTest
    @Test
    fun test_Action_Bar_Not_Null() {
        val activity = rule.activity
        val actionbar = activity.actionBar
        assertNotNull(actionbar)
    }

    @SmallTest
    @Test
    fun test_ActionBar_Tittle_Changes() {
        val activity = rule.activity
        val actionbar_Text = activity.actionBar?.title
        assertNotNull("Github Issues", actionbar_Text)
    }

    @SmallTest
    @Test
    fun test_ListViewItems_Are_All_Enabled() {
        val activity = rule.activity
        val listview = activity.findViewById<ListView>(R.id.recycler1)
        val boolean_Result = listview.adapter.areAllItemsEnabled()
        assertTrue(boolean_Result)
    }

}
*/
