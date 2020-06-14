package smartherd.kenyamessagesolution.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_other_files.*
import smartherd.kenyamessagesolution.Adapteres.bring
import smartherd.kenyamessagesolution.Extensions.makeLongToast
import smartherd.kenyamessagesolution.R
import java.io.Serializable


class OtherFiles : AppCompatActivity() {

    private var therowname: String = ""
    private var therownumer: String = ""
    private var anotherRow: ArrayList<bring> = ArrayList<bring>()
    private var letersornumbers = "Letters"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_files)

        initall()
    }

    private fun initall() {

        theswitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, _ ->
            // do something, the isChecked will be
            letersornumbers = "Numbers"
        })

        add.setOnClickListener {
            //Validation
            if (onerowname.text.toString().trim().equals("")) {
                makeLongToast("You cannot leave the row name blank")
            } else if (onerownumber.text.toString().trim().equals("")) {
                makeLongToast("You cannot leave the row number blank")
            } else {
                therowname = onerowname.text.toString().trim()
                therownumer = onerownumber.text.toString().trim()
                anotherRow.add(bring(therowname, therownumer, letersornumbers))
                AlertDialogForConfirmation()
            }
        }
    }

    private fun AlertDialogForConfirmation() {

        var theitems: String = ""

        for (item in anotherRow) {
            theitems = theitems + "\nRow: " + item.rowname + " No: " + item.rownumber + " Type: " + item.numbersOrletters

        }

         AlertDialog.Builder(this)
            .setTitle("KMS")
            .setCancelable(false)
            .setMessage(
                "The following selections will be added in rows and columns ${theitems}\n"
            )
            .setIcon(R.drawable.mainicon)
            .setPositiveButton("Finish",
                DialogInterface.OnClickListener { dialog, _ ->
                    returnToPreviousActivityWithTheDataInIntent()
                    dialog.dismiss()
                })
            .setNeutralButton("Reselect|Clear", DialogInterface.OnClickListener { dialog, _ ->
                onerowname.text.clear()
                onerownumber.text.clear()
                if (theswitch.isChecked) theswitch.setChecked(false)
                anotherRow.clear()
                dialog.dismiss()
            })

            .setNegativeButton("Add Rows",
                DialogInterface.OnClickListener { dialog, _ ->
                    onerowname.text.clear()
                    onerownumber.text.clear()
                    if (theswitch.isChecked) theswitch.setChecked(false)
                    dialog.dismiss()
                    dialog.dismiss()
                })
            .show()
    }

    private fun returnToPreviousActivityWithTheDataInIntent() {
        // intialize Bundle instance
        val b = Bundle()
        b.putSerializable("questions", anotherRow as Serializable?)
        val i = Intent(this, OneMainActivity::class.java)
        i.putExtras(b)
        startActivity(i)
        finish()
    }
}


