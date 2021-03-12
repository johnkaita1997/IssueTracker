package smartherd.githubissuetracker.Extensions

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import smartherd.githubissuetracker.R

private lateinit var redirectingDialog: ProgressDialog
var alert: AlertDialog? = null


fun Context.makeLongToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.goToActivity(activity: Activity, classs: Class<*>?) {
    val intent = Intent(activity, classs)
    startActivity(intent)
    activity.finish()
}

fun Context.showredirect() {
    redirectingDialog = ProgressDialog(this)
    redirectingDialog.setMessage("Redirecting...")
    redirectingDialog.show()
}

fun Context.dismissredirect() {
    if (redirectingDialog.isShowing) redirectingDialog.dismiss()
}

fun Context.showAlertDialog(message: String) {
    alert = AlertDialog.Builder(this)
        .setTitle("KMS")
        .setCancelable(false)
        .setMessage(message)
        .setIcon(R.drawable.mainicon)
        .setPositiveButton("",
            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
        .setNegativeButton("OKAY",
            DialogInterface.OnClickListener { dialog, _ -> dialog.dismiss() })
        .show()
}

fun Context.dissmissAlertDialogMessage() {
    alert?.dismiss()

}