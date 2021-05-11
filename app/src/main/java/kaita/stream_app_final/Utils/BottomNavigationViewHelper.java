package kaita.stream_app_final.Utils;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kaita.stream_app_final.Activities.Normal.MainActivity;
import kaita.stream_app_final.Fragments.General.HomeFragment;
import kaita.stream_app_final.Fragments.General.NotificationsFragment;
import kaita.stream_app_final.Fragments.General.ProfileFragment;
import kaita.stream_app_final.R;

public class BottomNavigationViewHelper {

    private Context context;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener;

    public BottomNavigationViewHelper(Context context){

        this.context=context;
        this.navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.home: selectedFragment = new HomeFragment(); break;
                    case R.id.profile: selectedFragment = new ProfileFragment(); break;
                    case R.id.notifications : selectedFragment = new NotificationsFragment(); break;
                }
                ((MainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        };

    }

}
