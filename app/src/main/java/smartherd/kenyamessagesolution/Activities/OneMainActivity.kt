package smartherd.kenyamessagesolution.Activities

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import hotchemi.android.rate.AppRate
import kotlinx.android.synthetic.main.activity_one_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smartherd.kenyamessagesolution.Extensions.dissmissAlertDialogMessage
import smartherd.kenyamessagesolution.Extensions.goToActivity
import smartherd.kenyamessagesolution.Extensions.showAlertDialog
import smartherd.kenyamessagesolution.R

class OneMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mtabLayout: TabLayout
    private lateinit var mPagerAdapterrentor: Any
    private lateinit var mViewPager: ViewPager
    private lateinit var auth: FirebaseAuth
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mtoggle: ActionBarDrawerToggle
    private lateinit var nv: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_main)

        initall()
        configureToolbar()
        navigationclicklistenre()

    }

    private fun initall() {
        auth = FirebaseAuth.getInstance()

        //IO, Main and Default
        CoroutineScope(Dispatchers.IO).launch {
            heaylayoutdrawing()
        }
    }

    private suspend fun heaylayoutdrawing() {
        mDrawerLayout = findViewById(R.id.drawerlayout) as DrawerLayout
        mtoggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close)
        mDrawerLayout.addDrawerListener(mtoggle)
        nv = findViewById(R.id.nview) as NavigationView
        mtoggle.syncState()

        mViewPager = findViewById(R.id.tab_Pager) as ViewPager
        mPagerAdapterrentor = OneRentorSectionPagerAdapter(supportFragmentManager)
        mtabLayout = findViewById(R.id.main_tabs) as TabLayout
        mtabLayout.setupWithViewPager(mViewPager)
        //mtabLayout.getTabAt(0).setIcon(R.drawable.iconcd);
        // mtabLayout.getTabAt(1).setIcon(R.drawable.iconab);

        mViewPager.setAdapter(mPagerAdapterrentor as OneRentorSectionPagerAdapter)
        // mViewPager.setPageTransformer(true, new RotateUpTransformer());
        // mViewPager.setPageTransformer(true, new RotateUpTransformer());
        mViewPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                (mPagerAdapterrentor as OneRentorSectionPagerAdapter).notifyDataSetChanged()
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    private fun configureToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_main) as Toolbar
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.setHomeAsUpIndicator(R.drawable.menu_icon)
        actionbar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun navigationclicklistenre() {
        nview.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.aboutGomessanger) {
            showdialog()
        } else if (item.itemId == R.id.contactus) {
            showAlertDialog("We love to hear from you: \n\n Email: kenyamessagesolution@gmail.com\n Mobile: 0729522550")
        } else if (item.itemId == R.id.share) {
            shareApp()
        } else if (item.itemId == R.id.logout) {
            auth.signOut()
            goToActivity(this, OneLoginActivity::class.java)
        }
        return true
    }

    private fun rateApp() {
        AppRate.with(this)
            .setInstallDays(3)
            .setLaunchTimes(2)
            .setRemindInterval(5)
            .monitor()
        AppRate.showRateDialogIfMeetsConditions(this)
    }

    private fun shareApp() {
        val a = Intent(Intent.ACTION_SEND)
        //this is to get the app link in the playstore without launching your app.
        val appPackageName = applicationContext.packageName
        var strAppLink : String
        strAppLink = try {
            "https://play.google.com/store/apps/details?id=$appPackageName"
        } catch (anfe: ActivityNotFoundException) {
            "https://play.google.com/store/apps/details?id=$appPackageName"
        }
        // this is the sharing part
        a.type = "text/link"
        val shareBody = """
            KMS : Kenya's finest messaging solution
            $strAppLink
            """.trimIndent()
        val shareSub = "Hirepap"
        a.putExtra(Intent.EXTRA_SUBJECT, shareSub)
        a.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(a, "Share Using"))
    }

    private fun showdialog() {
        showAlertDialog("This is an advanced intelligent system that developed with the latest technology to help aspiring leaders manage their campaigns and organizations to reach out out to their clients and employees.")
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mtoggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mtoggle.onConfigurationChanged(newConfig)
    }

    //Initiating the menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mtoggle.onOptionsItemSelected(item)) {
            return true
        } else if (item.itemId == R.id.essanger) {
            Toast.makeText(
                this,
                "You will be updated with new features and improvements",
                Toast.LENGTH_SHORT
            ).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dissmissAlertDialogMessage()
    }

    override fun onPause() {
        super.onPause()
        dissmissAlertDialogMessage()
    }

    override fun onResume() {
        super.onResume()
        dissmissAlertDialogMessage()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.sexmenu, menu)
        //setMenuBackground();
        // MenuItem thisItem = menu.findItem(R.id.app_bar_search);
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.essanger -> {
                Toast.makeText(this, "Workd", Toast.LENGTH_SHORT).show()
                false
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        val alertDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setIcon(R.drawable.mainicon)
            .setTitle("Confirm")
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { _, _ -> //deletetherequestifnotactive();
                    System.exit(0)
                })
            .setNegativeButton("No", null)
            .show()
        val btnPositive: Button = alertDialog!!.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative: Button = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val layoutParams =
            btnPositive.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative.layoutParams = layoutParams
    }

}

