package kaita.stream_app_final.Adapteres;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import kaita.stream_app_final.Fragments.ProfileFragments.EditProfile;
import kaita.stream_app_final.Fragments.ProfileFragments.ProfileOperations;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                EditProfile editProfile = new EditProfile();
                return  editProfile;
            case 1:
                ProfileOperations profileoperations  = new ProfileOperations();
                return profileoperations;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Edit Profile";
            case 1:
                return "Profile Operations";
            default:
                return null;
        }
    }
}


