package kaita.stream_app_final.Fragments.ProfileFragments

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import kaita.stream_app_final.Activities.Authentication.SignUpActivity
import kaita.stream_app_final.Activities.ProfileOperations.ParticipateBets
import kaita.stream_app_final.Activities.ProfileOperations.ShowStreamList
import kaita.stream_app_final.Adapteres.CustomCountryList
import kaita.stream_app_final.Adapteres.setSafeOnClickListener
import kaita.stream_app_final.AppConstants.Constants.firebaseAuth
import kaita.stream_app_final.AppConstants.Constants.googleSignInClient
import kaita.stream_app_final.Extensions.goToActivity
import kaita.stream_app_final.Extensions.goToActivity_Unfinished
import kaita.stream_app_final.R
import kotlinx.android.synthetic.main.fragment_profileoperations.view.*


class ProfileOperations : Fragment() {

    private val listView: ListView? = null
    private lateinit var source: View
    private val countryNames = arrayOf("Your Streams", "Your Bets")
    private val imageid = arrayOf<Int>(
        R.drawable.person,
        R.drawable.personb
    )

    @Nullable
    override fun onCreateView(inflater: LayoutInflater,@Nullable container: ViewGroup?,@Nullable savedInstanceState: Bundle?): View? {
        source = inflater.inflate(R.layout.fragment_profileoperations, container, false)
        initall()
        return source
    }

    private fun initall() {
        customTextView_For_Header()
        logoutClickListener()
    }

    private fun logoutClickListener() {
        source.logout.setSafeOnClickListener {
            if (firebaseAuth.currentUser != null) {
                firebaseAuth.signOut()
            }
            if (LoginManager.getInstance() != null) {
                LoginManager.getInstance().logOut();
            }
            if (googleSignInClient != null) {
                googleSignInClient!!.signOut()
            }
            requireActivity().goToActivity(requireActivity(), SignUpActivity::class.java)
        }
    }

    private fun customTextView_For_Header() {

        val textView = TextView(requireContext())
        textView.setTypeface(Typeface.DEFAULT_BOLD)
        textView.text = "Select An Option"
        textView.gravity = Gravity.CENTER_HORIZONTAL

        val listView = source.list
        //listView.addHeaderView(textView)

        // For populating list data
        val customCountryList = CustomCountryList(requireActivity(), countryNames, imageid)
        listView.adapter = customCountryList

        listView.setOnItemClickListener { parent, view, position, id ->
            val selected = countryNames[position]

            when (selected) {
                "Your Streams" -> requireActivity().goToActivity_Unfinished(requireActivity(), ShowStreamList::class.java)
                "Your Bets" -> requireActivity().goToActivity_Unfinished(requireActivity(), ParticipateBets::class.java)
                else -> {
                    print("None")
                }
            }
        }

    }
}