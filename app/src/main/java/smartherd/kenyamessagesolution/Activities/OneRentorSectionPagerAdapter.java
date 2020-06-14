package smartherd.kenyamessagesolution.Activities;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import Fragments.OneAllcontants;
import Fragments.OneCompose;
import Fragments.Smsgroups;

/**
 * Created by pk on 9/4/2018.
 */

class OneRentorSectionPagerAdapter extends FragmentPagerAdapter {
    public OneRentorSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        //Try and do that logic from  here.
        if (position == 1) {
            OneCompose oneCompose = new OneCompose();
            return oneCompose;
        }

        if (position == 0) {
            OneAllcontants oneAllcontants = new OneAllcontants();
            return oneAllcontants;

        }else{
            Smsgroups smsgroups = new Smsgroups();
            return smsgroups;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
                return "CONTACTS";

            case 1:
                return "COMPOSE";

            case 2:
                return "SMS GROUPS";

            default:
                return null;
        }
    }
}


