package kaita.stream_app_final.Fragments.General

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.SectionPagerAdapter
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.Fragments.ProfileFragments.EditProfile
import kaita.stream_app_final.Fragments.ProfileFragments.ProfileOperations
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var source : View
    private lateinit var mPagerAdapter: SectionPagerAdapter
    private lateinit var fragment: Fragment
    private lateinit var fm: FragmentManager
    private lateinit var ft: FragmentTransaction

    @Nullable
    override fun onCreateView( inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle? ): View? {
        source = inflater.inflate(R.layout.fragment_profile, container, false)
        initall()

        return source
    }

    @SuppressLint("ResourceAsColor")
    private fun initall() {

        show_Number

        FirebaseChecker().load_selected_Streamer_Stream(firebaseAuth.currentUser.uid){
            if (it.exists()) {
                source.show_Number.setText("1")
            } else {
                source.show_Number.setText("0")
            }
        }

        fm = childFragmentManager
        ft = fm.beginTransaction()

        if (source != null && requireActivity() != null && !R.id.fragment_holder.equals(null)) {
            ft.add(R.id.fragment_holder, EditProfile(), "Edit Profile")
            ft.commit()

            source.edit_Profile.setSafeOnClickListener {
                fragment = EditProfile()
                ft = fm.beginTransaction()
                ft.replace(R.id.fragment_holder, fragment)
                ft.commit()
            }

            source.profile_operations.setSafeOnClickListener {
                ft = fm.beginTransaction()
                ft = fm.beginTransaction()
                fragment = ProfileOperations()
                ft.replace(R.id.fragment_holder, fragment)
                ft.commit()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            setupViewPager()
        }
    }

    private fun setupViewPager() {
      /*  mPagerAdapter =  SectionPagerAdapter(requireActivity().supportFragmentManager);
        source.tab_Pager.setAdapter(mPagerAdapter)
        source.tab_Pager.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                mPagerAdapter.notifyDataSetChanged()
            }
            override fun onPageScrollStateChanged(i: Int) {}
        })
        source.main_tabs.setupWithViewPager(tab_Pager)
        source.main_tabs.setTabTextColors(ColorStateList.valueOf(resources.getColor(R.color.red_400)))
        source.main_tabs.setSelectedTabIndicator(R.color.red_400)*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager()
    }
}