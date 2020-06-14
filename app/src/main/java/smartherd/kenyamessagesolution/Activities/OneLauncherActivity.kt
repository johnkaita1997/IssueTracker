package smartherd.kenyamessagesolution.Activities

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.DoubleBounce
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_one_launcher.*
import kotlinx.android.synthetic.main.fragment_one_allcontants.*
import kotlinx.android.synthetic.main.fragment_one_allcontants.view.*
import kotlinx.android.synthetic.main.fragment_one_allcontants.view.progress
import smartherd.kenyamessagesolution.AppConstants.Constants.permission_request
import smartherd.kenyamessagesolution.Extensions.alert
import smartherd.kenyamessagesolution.Extensions.goToActivity
import smartherd.kenyamessagesolution.Extensions.showAlertDialog
import smartherd.kenyamessagesolution.R


class OneLauncherActivity : AppCompatActivity() {


    var cd = ConnectionDetector(this)
    private lateinit var progressBar: ProgressBar
    var doubleBounce: Sprite = DoubleBounce()


    //All the permissions
    var PERMISSIONS = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_SMS,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_launcher)

        initall()

    }

    private fun initall() {

        progressBar = progresssec
        progressBar.indeterminateDrawable = doubleBounce
        progressBar.setVisibility(View.VISIBLE)

        //Ask for permissions first
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS
            ),
            permission_request
        )
    }

    private fun internet_connection_error_Dilog() {
        //Internet connection error
        val alertDialog: android.app.AlertDialog? =
            android.app.AlertDialog.Builder(this)
                .setTitle("Network Error")
                .setMessage("This application requires an active internet connection for location and other services.")
                .setIcon(R.drawable.mainicon)
                .setPositiveButton("Fix",
                    DialogInterface.OnClickListener { _, _ -> /*  Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                        startActivityForResult(callGPSSettingIntent, 0);*/
                        val intent = Intent(Settings.ACTION_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    })
                .setNegativeButton("Dismiss",
                    DialogInterface.OnClickListener { _, _ ->
                        finish()
                        System.exit(0)
                    })
                .show()
        val btnPositive: Button? = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNegative: Button? = alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)
        val layoutParams =
            btnPositive?.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
        btnNegative!!.layoutParams = layoutParams
    }

    fun gotonextpage() {
        val mAth = FirebaseAuth.getInstance()
        if (mAth.getCurrentUser() == null) {
            //Start timer for screen delay
            Handler().postDelayed({ //Go to the login activity
                goToActivity(this, OneLoginActivity::class.java)
            }, 2000)

        } else {
            //Check if user has paid
            var db = FirebaseFirestore.getInstance()
            db.collection(mAth.currentUser!!.email.toString()).document("Payment")
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        //User is logged in
                        Handler().postDelayed({ //Go to the login activity
                            goToActivity(this, OneMainActivity::class.java)
                        }, 2000)
                    } else {
                        alert = android.app.AlertDialog.Builder(this)
                            .setTitle("KMS")
                            .setCancelable(false)
                            .setMessage("This account is not associated with any payment.To use KMS, contact +254 729 522550")
                            .setIcon(R.drawable.mainicon)
                            .setPositiveButton("",
                                DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
                            .setNegativeButton("EXIT",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    System.exit(0)
                                    dialog.dismiss()
                                })
                            .show()
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (permission_request) {
            100 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Check for connectivity
                if (cd.isConnected) {
                    //Now go to the next page
                    gotonextpage()
                } else {
                    internet_connection_error_Dilog()
                }
            }
            else -> permissionError_Dialog()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun permissionError_Dialog() {
        val alertDialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this)
            .setMessage("This app might malfunction if all the permissions aren't granted.")
            .setCancelable(false)
            .setIcon(R.drawable.mainicon)
            .setTitle("Warning")
            .setPositiveButton("Dismiss",
                DialogInterface.OnClickListener { _, _ -> System.exit(0) })
            .setNegativeButton("", DialogInterface.OnClickListener { _, _ ->
                finish()
                System.exit(0)
            })
            .show()
        val btnPositive = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
        val layoutParams =
            btnPositive?.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10f
        btnPositive.layoutParams = layoutParams
    }
}

