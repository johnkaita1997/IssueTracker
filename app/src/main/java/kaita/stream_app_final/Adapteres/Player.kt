package kaita.stream_app_final.Adapteres

import android.os.SystemClock
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class Model {
    var created_at: String? = null
    var comments: String? = null
    var title: String? = null
    var developer: String? = null
    var commentload: String? = null
    var state: String? = null

    constructor() {}
    constructor(
        created_at: String?,
        comments: String?,
        title: String?,
        developer: String?,
        commentload: String?,
        state: String?
    ) {
        this.created_at = created_at
        this.comments = comments
        this.title = title
        this.developer = developer
        this.commentload = commentload
        this.state = commentload
    }

}


class Comments {
    var created_at: String? = null
    var body: String? = null
    var developer: String? = null

    constructor()
    constructor(body: String?, created_at: String?, developer: String?) {
        this.body = body
        this.created_at = created_at
        this.developer = developer
    }

}



class Scientist {
    var mId: String? = null
    var name: String? = null
    var description: String? = null

    constructor()
    constructor(name: String?, mId: String?, description: String?) {
        this.name = name
        this.mId = mId
        this.description = description
    }

}


public fun date_Converter (): String {
    var c = Calendar.getInstance()
    var daaaay = c.get(Calendar.DAY_OF_MONTH)
    var date = Date()
    var simpleDateFormat = SimpleDateFormat("EEEE")
    var day = simpleDateFormat.format(date).toUpperCase()
    simpleDateFormat = SimpleDateFormat("MMMM")
    var month = simpleDateFormat.format(date).toUpperCase()
    val final_text = "Today $daaaay, $day, $month"
    return final_text
}


data class Tittle(
    var title: String? = null,
    var comments: String? = null
)


class SafeClickListener(
    private var defaultInterval: Int = 1,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}