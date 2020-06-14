package Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_smsgroups.*
import kotlinx.android.synthetic.main.fragment_smsgroups.view.*
import kotlinx.android.synthetic.main.fragment_smsgroups.view.groupname
import smartherd.kenyamessagesolution.Extensions.makeLongToast
import smartherd.kenyamessagesolution.Extensions.showredirect
import smartherd.kenyamessagesolution.R
import java.util.*

class Smsgroups : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var oneSearchValue: String
    private lateinit var onegroupName: String
    private lateinit var viewy: View
    private val brands: LinkedList<String> = LinkedList<String>()
    private var mArrayList: ArrayList<Any> = ArrayList()
    private lateinit var menu: PopupMenu
    private var searchOption = ""
    private val mFirestore = FirebaseFirestore.getInstance()

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
        auth = FirebaseAuth.getInstance()
        viewy.chooseOption.setOnClickListener {
            loadAllTheItems()
            menu = PopupMenu(activity, it)
        }

        viewy.sendtoGroup.setOnClickListener {
            //Validate the group name and search query
            onegroupName = groupname.text.toString().trim()
            oneSearchValue = searchVal.text.toString().trim()
            if (onegroupName.length.equals(0)) activity?.makeLongToast("You cannot leave the group name blank")
            else if (oneSearchValue.length.equals(0)) activity?.makeLongToast("You cannot leave the search value blank")
            else if (searchOption.equals("")) activity?.makeLongToast("You have to select an option")
            else {
                //Now save the value to the database
                saveToTheDatabase(onegroupName, oneSearchValue, searchOption)
            }
        }
    }

    private fun saveToTheDatabase(
        onegroupName: String,
        oneSearchValue: String,
        searchOption: String
    ) {
        val mPostsCollection =
            mFirestore.collection(auth.currentUser!!.email.toString()).document("Groups")
                .collection("GroupList")
                .document(onegroupName)
        val note: MutableMap<String, Any> = HashMap()
        note["groupname"] = onegroupName
        note["startswith"] = oneSearchValue
        note["whereall"] = searchOption

        mPostsCollection.set(note)
            .addOnSuccessListener {
                activity?.makeLongToast("Group was saved sucessfully")
                groupname.text.clear()
                searchVal.text.clear()

            }.addOnFailureListener {
                activity?.makeLongToast("Group was not saved, try again!")
            }
    }

    private fun loadAllTheItems() {

        Log.d("mate", "Here")
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection(auth.currentUser!!.email.toString()).document("Keys")

        // do something
        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val deviceNameList = arrayListOf<String>()
                Log.d("mate", "DocumentSnapshot data: ${document.data?.get("allkeys").toString()}")
                mArrayList = arrayListOf(document.data?.get("allkeys")) as ArrayList<Any>
                mArrayList.forEach {
                    deviceNameList.add(it.toString())
                }

                val input = deviceNameList[0]
                val names: Array<String> =
                    input.substring(1, input.length - 1).split(",\\s*").toTypedArray()

                //This prints all the values in an array
                for (x in names[0].split(",")) {
                    menu.menu.add(x)
                }

                menu.setOnMenuItemClickListener(this)
                menu.show()

            } else {
                activity?.makeLongToast("No data available")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("mate", "get failed with ", exception)
            }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        //How do i add
        searchOption = item.title.toString()
        chooseOption.text = item.title.toString()
        return true
    }
}
