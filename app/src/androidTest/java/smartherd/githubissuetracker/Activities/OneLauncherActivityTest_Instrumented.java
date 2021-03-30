package smartherd.githubissuetracker.Activities;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import com.github.ybq.android.spinkit.sprite.Sprite;

import org.junit.Rule;
import org.junit.Test;

import smartherd.githubissuetracker.R;

import static org.junit.Assert.assertNotNull;


public class OneLauncherActivityTest_Instrumented {

    @Rule
    public ActivityTestRule<OneLauncherActivity> rule  = new  ActivityTestRule<>(OneLauncherActivity.class);

    @SmallTest
    @Test
    public void test_ImageView_Not_Null() {
        OneLauncherActivity activity = rule.getActivity();
        ImageView imageView = activity.findViewById(R.id.imageView);
        assertNotNull(imageView);
    }

    @SmallTest
    @Test
    public void test_TextView_AppName_Not_Null() {
        OneLauncherActivity activity = rule.getActivity();
        TextView textView = activity.findViewById(R.id.textView3);
        assertNotNull(textView);
    }

    @SmallTest
    @Test
    public void test_TextView_By_Not_Null() {
        OneLauncherActivity activity = rule.getActivity();
        TextView textView = activity.findViewById(R.id.textView433);
        assertNotNull(textView);
    }


    @SmallTest
    @Test
    public void test_TextView_Developer_Name_Not_Null() {
        OneLauncherActivity activity = rule.getActivity();
        TextView textView = activity.findViewById(R.id.textView4);
        assertNotNull(textView);
    }

    @SmallTest
    @Test
    public void test_animation_Not_Equal_To_Null() {
        OneLauncherActivity activity = rule.getActivity();
        ProgressBar progressBar = activity.findViewById(R.id.progresssec);
        assertNotNull(progressBar);
    }


    @SmallTest
    @Test
    public void test_double_Not_Null() {
        OneLauncherActivity activity = rule.getActivity();
        Sprite doubleBounce = activity.getDoubleBounce();
        assertNotNull(doubleBounce);
    }



}
