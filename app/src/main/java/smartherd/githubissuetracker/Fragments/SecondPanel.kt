package smartherd.githubissuetracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import smartherd.githubissuetracker.R
import java.util.*

class Smsgroups : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var oneSearchValue: String
    private lateinit var onegroupName: String
    private lateinit var viewy: View
    private val brands: LinkedList<String> = LinkedList<String>()
    private var mArrayList: ArrayList<Any> = ArrayList()
    private lateinit var menu: PopupMenu
    private var searchOption = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewy = inflater.inflate(R.layout.fragment_smsgroups, container, false)

        initall()

        return viewy
    }

    private fun initall() {

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}
