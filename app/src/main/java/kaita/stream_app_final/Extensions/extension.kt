package kaita.stream_app_final.Extensions

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.LinearLayout
import android.widget.Toast
import kaita.stream_app_final.R


private lateinit var redirectingDialog: ProgressDialog
var alert: AlertDialog? = null


fun Context.makeLongToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.makeShortToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.goToActivity(activity: Activity, classs: Class<*>?) {
    val intent = Intent(activity, classs)
    startActivity(intent)
    activity.finish()
}

fun Context.goToActivity_Unfinished(activity: Activity, classs: Class<*>?) {
    val intent = Intent(activity, classs)
    startActivity(intent)
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


fun Context.showAlertDialog_Special(alertDialog: AlertDialog, title: String, message: String, okaybuttonName:String, bar: () -> Unit) {
    alertDialog.setTitle(title)
    alertDialog.setIcon(R.drawable.mainicon)
    alertDialog.setMessage(message)
    alertDialog.setCancelable(false)
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss") { dialog, which ->
        dialog.dismiss()
    }
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, okaybuttonName) { dialog, which ->
        bar()
        dialog.dismiss()
    }

    if (alertDialog.isShowing) {
        alertDialog.dismiss()
    }

    alertDialog.show()

    val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
    val btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

    val layoutParams = btnPositive.layoutParams as LinearLayout.LayoutParams
    layoutParams.weight = 10f
    btnPositive.layoutParams = layoutParams
    btnNegative.layoutParams = layoutParams

}



fun Context.dissmissAlertDialogMessage() {
    alert?.dismiss()

}