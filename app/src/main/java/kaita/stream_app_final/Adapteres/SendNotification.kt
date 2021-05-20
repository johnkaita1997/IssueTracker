package kaita.stream_app_final.Adapteres

import android.util.Log
import kaita.stream_app_final.AppConstants.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

fun sendNotification(receiverID: String?, message: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.d("Mbwa", "sendNotification:  $receiverID")
            val jsonResponse: String
            val url = URL("https://onesignal.com/api/v1/notifications")
            val con = url.openConnection() as HttpURLConnection
            con.useCaches = false
            con.doOutput = true
            con.doInput = true
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            con.setRequestProperty("Authorization", "Basic MzcyNmIxNTgtZWM4My00ZTYyLWFmNmEtNmZjOGNhNjFhNjY4")
            con.requestMethod = "POST"
            val strJsonBody = ("{"
                    + "\"app_id\": \"${Constants.ONESIGNAL_APP_ID}\","
                    + "\"filters\": [{\"field\": \"tag\", \"key\": \"$receiverID\", \"relation\": \"=\", \"value\": \"" + receiverID + "\"}],"
                    + "\"data\": {\"foo\": \"bar\"},"
                    + "\"contents\": {\"en\": \"$message\"}"
                    + "}")
            println("strJsonBody:\n$strJsonBody")
            val sendBytes = strJsonBody.toByteArray(charset("UTF-8"))
            con.setFixedLengthStreamingMode(sendBytes.size)
            val outputStream = con.outputStream
            outputStream.write(sendBytes)
            val httpResponse = con.responseCode
            println("httpResponse: $httpResponse")
            if (httpResponse >= HttpURLConnection.HTTP_OK
                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST
            ) {
                val scanner = Scanner(con.inputStream, "UTF-8")
                jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                scanner.close()
            } else {
                val scanner = Scanner(con.errorStream, "UTF-8")
                jsonResponse = if (scanner.useDelimiter("\\A").hasNext()) scanner.next() else ""
                scanner.close()
            }
            println("jsonResponse:\n$jsonResponse")
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}