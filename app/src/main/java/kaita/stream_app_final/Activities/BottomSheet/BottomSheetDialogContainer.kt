package kaita.stream_app_final.Activities.BottomSheet

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kaita.stream_app_final.Adapteres.FirebaseChecker
import kaita.stream_app_final.Adapteres.SectionPagerAdapter
import kaita.stream_app_final.AppConstants.Constants
import kaita.stream_app_final.AppConstants.Constants.loaded
import kaita.stream_app_final.Fragments.BottomSheetFragments.BottomHomeFragment
import kaita.stream_app_final.R

class BottomSheetDialogContainer : BottomSheetDialogFragment() {

    private var mListener: BottomSheetListener? =  null
    private lateinit var source: View
    val options_Query = FirebaseChecker().homeRef_Streams.child(Constants.selected_id).child("options")
    val betsPlaced_Query = FirebaseChecker().homeRef_Streams.child(Constants.selected_id).child("bets")
    private lateinit var accessKey:String

    private lateinit var mPagerAdapter: SectionPagerAdapter
    private lateinit var fragment: Fragment
    private lateinit var fm: FragmentManager
    private lateinit var ft: FragmentTransaction

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View? {
        source = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        make_Bottom_Sheet_Full_Screen(dialog)
        initall()
        return source
    }

    private fun make_Bottom_Sheet_Full_Screen(dialog: Dialog?) {
        dialog!!.setOnShowListener {
            val d = dialog as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from<View?>(bottomSheetInternal!!).setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // Do custom work here
                    val fm: FragmentManager? = childFragmentManager
                    if (fm!!.getBackStackEntryCount() > 0) {
                        fm.popBackStack()
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun initall() {

        setupBackPressListener()

        accessKey = arguments?.getString("key").toString()

        val bundle = Bundle()
        bundle.putString("key", accessKey)

        val fragobj = BottomHomeFragment()
        fragobj.setArguments(bundle)
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fragobj)
        fragmentTransaction.commit()

    }

    interface BottomSheetListener {
        fun onButtonClicked(text: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as BottomSheetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()  + " must implement BottomSheetListener" )
        }
    }


    override fun onStart() {super.onStart()

        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
        Constants.mAdapter_Options.startListening();
        Constants.BetsPlaced_Adapter.startListening()
    }

    override fun onDetach() {
        super.onDetach()
        loaded = false
    }

    private fun setupBackPressListener() {
        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else
                false
        }
    }

}
