package smartherd.githubissuetracker.Activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Test
import smartherd.githubissuetracker.R

class OneLauncherActivityTest{

    @Test
    fun test_progress_Bar_Is_Made_Visible() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(OneLauncherActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(withId(R.id.progresssec))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun test_The_Button_Is_Header_Image_Is_Visibile() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(OneLauncherActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(withId(R.id.imageView))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun test_Other_Textview_A() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(OneLauncherActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(withId(R.id.textView3))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    @Test
    fun test_Other_Textview_B() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(OneLauncherActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(withId(R.id.textView4))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


     @Test
    fun test_Other_Textview_C() {
        //Call the name of the activity, then launch it and check whether is has been displayed
        val activityscenario = ActivityScenario.launch(OneLauncherActivity::class.java)
        //We are finding some view with some id
        Espresso.onView(withId(R.id.textView433))
            //Now check for the visibility
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}

