package kaita.stream_app_final.AppConstants

import android.app.AlertDialog
import android.view.View
import androidx.paging.PagedList
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import dmax.dialog.SpotsDialog
import kaita.stream_app_final.Activities.Modals.BetsPlaced
import kaita.stream_app_final.Activities.Modals.BetsPlaced_Final
import kaita.stream_app_final.Activities.Modals.Options
import kaita.stream_app_final.Activities.Modals.Post
import kaita.stream_app_final.Adapteres.*
import kaita.stream_app_final.Adapteres.Categories.Category
import kaita.stream_app_final.Adapteres.Categories.CategoryViewHolder
import kaita.stream_app_final.Adapteres.Complaints.ComplainViewHolder
import kaita.stream_app_final.Adapteres.Complaints.Complained
import kaita.stream_app_final.Adapteres.CreatePost.CreatePost
import kaita.stream_app_final.Adapteres.CreatePost.CreatePostViewHolder
import kaita.stream_app_final.Adapteres.EndBet.EndBet
import kaita.stream_app_final.Adapteres.EndBet.EndBet_ViewHolder
import kaita.stream_app_final.Adapteres.Expectingpayment.ExpectingPaymented
import kaita.stream_app_final.Adapteres.Expectingpayment.ExpectingPaymentedViewHolder
import kaita.stream_app_final.Adapteres.Finish.Finished
import kaita.stream_app_final.Adapteres.Finish.FinishedViewHolder
import kaita.stream_app_final.Adapteres.ShowStreamList.StreamList
import kaita.stream_app_final.Adapteres.ShowStreamList.StreamListViewHolder

object Constants {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              PAGINATION START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val mPosts = 5 //The number of posts to be viewed on each screen
    val database: FirebaseFirestore = FirebaseFirestore.getInstance()
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //              Paypal START
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    val clientKey = "ARx3ElFHpFsAQibnzRpviaL63QZ4pmPzU1bUi3o1L6BvIPwtn0SU-CvtnODTSKWL9ecAVy1HKzJoMdEb"
    val configd =  PayPalConfiguration()
        // switch to sandbox (ENVIRONMENT_SANDBOX)
        // or live (ENVIRONMENT_PRODUCTION)
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId(clientKey);
    val PAYPAL_REQUEST_CODE = 123;















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
        .setPageSize(2)
        .build()
    public lateinit var mAdapter: FirestorePagingAdapter<Post, HomeFragmentViewHolder>

    public lateinit var complainsAdapter: FirebaseRecyclerAdapter<Complained, ComplainViewHolder>

    public lateinit var expectionPaymentAdapter: FirebaseRecyclerAdapter<ExpectingPaymented, ExpectingPaymentedViewHolder>

    public lateinit var finishAdapter: FirebaseRecyclerAdapter<Finished, FinishedViewHolder>

    public lateinit var categories_Adapter: FirebaseRecyclerAdapter<Category, CategoryViewHolder>

    public lateinit var createPostAdapter: FirebaseRecyclerAdapter<CreatePost, CreatePostViewHolder>

    public lateinit var streamList_Adapter: FirebaseRecyclerAdapter<StreamList, StreamListViewHolder>


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
    lateinit var thedatabaseReference: DatabaseReference
    lateinit var thebetamount: String
    var alist = mutableListOf<String>()

}