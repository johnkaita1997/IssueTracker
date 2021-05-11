package kaita.stream_app_final.Activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import kaita.stream_app_final.Activities.Normal.MainActivity
import kaita.stream_app_final.R
import org.junit.Test

class MainActivityTest {

    @Test
    fun test_date_Button_Is_Clickable() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.thedate))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

  @Test
    fun test_Reload_Button_Is_Clickable() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.reload))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    @Test
    fun test_Search_Bar_Is_Visible() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.search))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun is_date_Button_Visible() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.thedate))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_reload_button_is_Visible() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.reload))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun test_Today_Text_Is_Visible() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.today))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

 @Test
    fun test_ListView_Is_Visible() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(MainActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(ViewMatchers.withId(R.id.recycler1))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}