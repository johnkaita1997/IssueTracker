package smartherd.githubissuetracker.Activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import smartherd.githubissuetracker.Fragments.HomePanel;
import smartherd.githubissuetracker.Fragments.Smsgroups;

class OneRentorSectionPagerAdapter extends FragmentPagerAdapter {
    public OneRentorSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            HomePanel homepanel = new HomePanel();
            return homepanel;

        } else {
            Smsgroups smsgroups = new Smsgroups();
            return smsgroups;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
                return "FLUTTER  ISSUES";
            default:
                return "OTHER";
        }
    }

}


