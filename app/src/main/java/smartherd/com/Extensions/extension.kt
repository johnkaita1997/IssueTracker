package smartherd.com.Extensions

import android.content.Context
import android.widget.Toast

fun Context.makeLongToast(message : String, duration : Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}