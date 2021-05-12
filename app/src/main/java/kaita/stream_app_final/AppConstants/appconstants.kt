package kaita.stream_app_final.AppConstants

import android.app.AlertDialog
import android.view.View
import androidx.paging.PagedList
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Modals.*
import kaita.stream_app_final.Adapteres.*

object Constants {
    //These are all the constants within our application
    var loaded = false
    const val permission_request = 100
    val firebaseAuth = FirebaseAuth.getInstance()
    val streams = FirebaseChecker().homeRef_Streams
    lateinit  var selected_id: String
    val fname = "name"
    val fmobile = "mobileNumber"
    val fdpurl = "dpurl"
    val femail = "email"
    public lateinit var progressDialog: SpotsDialog
    var closed__open: String = ""
    // Init Paging Configuration
    val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPrefetchDistance(2)
        .setPageSize(20)
        .build()
    public lateinit var mAdapter: FirebaseRecyclerPagingAdapter<Post, HomeFragmentViewHolder>
    public lateinit var options : DatabasePagingOptions<Post>

    public lateinit var options_Second : DatabasePagingOptions<Options>
    public lateinit var mAdapter_Options: FirebaseRecyclerPagingAdapter<Options, OptionsViewHolder>

    public lateinit var options_End: DatabasePagingOptions<EndBet>
    public lateinit var mAdapter_Options_End: FirebaseRecyclerPagingAdapter<EndBet, EndBet_ViewHolder>

    public lateinit var BetsPlaced_Paging : DatabasePagingOptions<BetsPlaced>
    public lateinit var BetsPlaced_Adapter: FirebaseRecyclerPagingAdapter<BetsPlaced, BetsPlacedViewHolder>

    public lateinit var BetsPlaced_Paging_Final : DatabasePagingOptions<BetsPlaced_Final>
    public lateinit var BetsPlaced_Adapter_Final: FirebaseRecyclerPagingAdapter<BetsPlaced_Final, BetsPlacedViewHolder_Final>

    var hashMap_Selected_Bet_By_Better = HashMap<String, Any>()
    lateinit var source : View
    val alphabetical_List  = listOf("a", "b", "c", "d", "e", "f", "g",  "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
    var chosen_Answer = ""
    var googleSignInClient: GoogleSignInClient? = null
    const val ONESIGNAL_APP_ID = "52a2c790-173c-4606-a0a6-941f3b4d58eb"
    lateinit var alertDialog: AlertDialog

}